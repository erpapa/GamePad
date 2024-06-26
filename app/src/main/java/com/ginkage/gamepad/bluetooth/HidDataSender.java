/*
 * Copyright 2018 Google LLC All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ginkage.gamepad.bluetooth;

import static com.google.common.base.Preconditions.checkNotNull;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import androidx.annotation.MainThread;
import android.util.ArraySet;
import android.util.Log;
import com.ginkage.gamepad.bluetooth.HidDeviceProfile.ServiceStateListener;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/** Central point for enabling the HID SDP record and sending all data. */
public class HidDataSender implements GamepadReport.GamepadDataSender {

    private static final String TAG = "HidDataSender";

    /** Compound interface that listens to both device and service state changes. */
    public interface ProfileListener
            extends HidDeviceApp.DeviceStateListener, ServiceStateListener {}

    private final ProfileListener profileListener =
            new ProfileListener() {
                @Override
                @MainThread
                public void onServiceStateChanged(BluetoothHidDevice proxy) {
                    synchronized (lock) {
                        if (proxy == null) {
                            if (isAppRegistered) {
                                // Service has disconnected before we could unregister the app.
                                // Notify listeners, update the UI and internal state.
                                onAppStatusChanged(null, false);
                            }
                        } else {
                            hidDeviceApp.registerApp(proxy);
                        }
                        updateDeviceList();
                        for (ProfileListener listener : listeners) {
                            listener.onServiceStateChanged(proxy);
                        }
                    }
                }

                @Override
                @MainThread
                public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                    synchronized (lock) {
                        if (isAppRegistered == registered) {
                            // We are already in the correct state.
                            return;
                        }
                        isAppRegistered = registered;

                        for (ProfileListener listener : listeners) {
                            listener.onAppStatusChanged(pluggedDevice, registered);
                        }
                        if (registered && waitingForDevice != null) {
                            // Fulfill the postponed request to connect.
                            requestConnect(waitingForDevice);
                        }
                    }
                }

                @Override
                @MainThread
                public void onConnectionStateChanged(BluetoothDevice device, int state) {
                    synchronized (lock) {
                        if (state == BluetoothProfile.STATE_CONNECTED) {
                            // A new connection was established. If we weren't expecting that, it
                            // must be an incoming one. In that case, we shouldn't try to disconnect
                            // from it.
                            waitingForDevice = device;
                        } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                            // If we are disconnected from a device we are waiting to connect to, we
                            // ran into a timeout and should no longer try to connect.
                            if (device == waitingForDevice) {
                                waitingForDevice = null;
                            }
                        }
                        updateDeviceList();
                        for (ProfileListener listener : listeners) {
                            listener.onConnectionStateChanged(device, state);
                        }
                    }
                }

                @Override
                @MainThread
                public void onGetReport(BluetoothDevice device, byte type, byte id, int bufferSize) {
                    for (ProfileListener listener : listeners) {
                        listener.onGetReport(device, type, id, bufferSize);
                    }
                }

                @Override
                @MainThread
                public void onSetReport(BluetoothDevice device, byte type, byte id, byte[] data) {
                    for (ProfileListener listener : listeners) {
                        listener.onSetReport(device, type, id, data);
                    }
                }

                @Override
                @MainThread
                public void onInterruptData(BluetoothDevice device, byte reportId, byte[] data) {
                    for (ProfileListener listener : listeners) {
                        listener.onInterruptData(device, reportId, data);
                    }
                }
            };

    static final class InstanceHolder {
        static final HidDataSender INSTANCE = createInstance();

        private static HidDataSender createInstance() {
            return new HidDataSender(new HidDeviceApp(), new HidDeviceProfile());
        }
    }

    private final BroadcastReceiver batteryReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onBatteryChanged(intent);
                }
            };

    private final HidDeviceApp hidDeviceApp;
    private final HidDeviceProfile hidDeviceProfile;

    private final Object lock = new Object();

    @GuardedBy("lock")
    private final Set<ProfileListener> listeners = new ArraySet<>();

    @GuardedBy("lock")
    @Nullable
    private BluetoothDevice connectedDevice;

    @GuardedBy("lock")
    @Nullable
    private BluetoothDevice waitingForDevice;

    @GuardedBy("lock")
    private boolean isAppRegistered;

    /**
     * @param hidDeviceApp HID Device App interface.
     * @param hidDeviceProfile Interface to manage paired HID Host devices.
     */
    private HidDataSender(HidDeviceApp hidDeviceApp, HidDeviceProfile hidDeviceProfile) {
        this.hidDeviceApp = checkNotNull(hidDeviceApp);
        this.hidDeviceProfile = checkNotNull(hidDeviceProfile);
    }

    /**
     * Retrieve the singleton instance of the class.
     *
     * @return Singleton instance.
     */
    public static HidDataSender getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Ensure that the HID Device SDP record is registered and start listening for the profile proxy
     * and HID Host connection state changes.
     *
     * @param context Context that is required to listen for battery charge.
     * @param listener Callback that will receive the profile events.
     * @return Interface for managing the paired HID Host devices.
     */
    @MainThread
    public HidDeviceProfile register(Context context, ProfileListener listener) {
        synchronized (lock) {
            if (!listeners.add(listener)) {
                // This user is already registered
                return hidDeviceProfile;
            }
            if (listeners.size() > 1) {
                // There are already some users
                return hidDeviceProfile;
            }

            context = checkNotNull(context).getApplicationContext();
            hidDeviceProfile.registerServiceListener(context, profileListener);
            hidDeviceApp.registerDeviceListener(profileListener);
            context.registerReceiver(
                    batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        return hidDeviceProfile;
    }

    /**
     * Stop listening for the profile events. When the last listener is unregistered, the SD record
     * for HID Device will also be unregistered.
     *
     * @param context Context that is required to listen for battery charge.
     * @param listener Callback to unregisterDeviceListener.
     */
    @MainThread
    public void unregister(Context context, ProfileListener listener) {
        synchronized (lock) {
            if (!listeners.remove(listener)) {
                // This user was removed before
                return;
            }
            if (!listeners.isEmpty()) {
                // Some users are still left
                return;
            }

            context = checkNotNull(context).getApplicationContext();
            context.unregisterReceiver(batteryReceiver);
            hidDeviceApp.unregisterDeviceListener();

            for (BluetoothDevice device : hidDeviceProfile.getConnectedDevices()) {
                hidDeviceProfile.disconnect(device);
            }

            hidDeviceApp.setDevice(null);
            hidDeviceApp.unregisterApp();

            hidDeviceProfile.unregisterServiceListener();

            connectedDevice = null;
            waitingForDevice = null;
        }
    }

    /**
     * Initiate connection sequence for the specified HID Host. If another device is already
     * connected, it will be disconnected first. If the parameter is {@code null}, then the service
     * will only disconnect from the current device.
     *
     * @param device New HID Host to connect to or {@code null} to disconnect.
     */
    @MainThread
    public void requestConnect(BluetoothDevice device) {
        synchronized (lock) {
            waitingForDevice = device;
            if (!isAppRegistered) {
                // Request will be fulfilled as soon the as app becomes registered.
                return;
            }

            connectedDevice = null;
            updateDeviceList();

            if (device != null && device.equals(connectedDevice)) {
                for (ProfileListener listener : listeners) {
                    listener.onConnectionStateChanged(device, BluetoothProfile.STATE_CONNECTED);
                }
            }
        }
    }

    /** Send the Gamepad data to the connected HID Host device. */
    @Override
    @MainThread
    public void sendGamepad(GamepadState state) {
        synchronized (lock) {
            if (connectedDevice != null) {
                hidDeviceApp.sendGamepad(state);
            }
        }
    }

    @MainThread
    private void updateDeviceList() {
        synchronized (lock) {
            BluetoothDevice connected = null;

            // If we are connected to some device, but want to connect to another (or disconnect
            // completely), then we should disconnect all other devices first.
            for (BluetoothDevice device : hidDeviceProfile.getConnectedDevices()) {
                if (device.equals(waitingForDevice) || device.equals(connectedDevice)) {
                    connected = device;
                } else {
                    hidDeviceProfile.disconnect(device);
                }
            }

            // If there is nothing going on, and we want to connect, then do it.
            if (hidDeviceProfile
                            .getDevicesMatchingConnectionStates(
                                    new int[] {
                                        BluetoothProfile.STATE_CONNECTED,
                                        BluetoothProfile.STATE_CONNECTING,
                                        BluetoothProfile.STATE_DISCONNECTING
                                    })
                            .isEmpty()
                    && waitingForDevice != null) {
                hidDeviceProfile.connect(waitingForDevice);
            }

            if (connectedDevice == null && connected != null) {
                connectedDevice = connected;
                waitingForDevice = null;
            } else if (connectedDevice != null && connected == null) {
                connectedDevice = null;
            }
            hidDeviceApp.setDevice(connectedDevice);
        }
    }

    @MainThread
    private void onBatteryChanged(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level >= 0 && scale > 0) {
            float batteryLevel = (float) level / (float) scale;
            hidDeviceApp.sendBatteryLevel(batteryLevel);
        } else {
            Log.e(TAG, "Bad battery level data received: level=" + level + ", scale=" + scale);
        }
    }
}
