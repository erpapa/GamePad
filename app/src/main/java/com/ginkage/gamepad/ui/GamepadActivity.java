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

package com.ginkage.gamepad.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.ginkage.gamepad.R;
import com.ginkage.gamepad.bluetooth.Constants;
import com.ginkage.gamepad.bluetooth.GamepadState;
import com.ginkage.gamepad.bluetooth.HidDataSender;

public class GamepadActivity extends AppCompatActivity {
    private static final int[] eightWay = {3, 2, 2, 1, 1, 8, 8, 7, 7, 6, 6, 5, 5, 4, 4, 3};

    private final GamepadState gamepadState = new GamepadState();

    private Vibrator hidVibrator;

    private HidDataSender hidDataSender;

    private HidDataSender.ProfileListener profileListener = new HidDataSender.ProfileListener() {
        @Override
        @MainThread
        public void onServiceStateChanged(BluetoothHidDevice proxy) {}

        @Override
        @MainThread
        public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
            if (!registered) {
                finish();
            }
        }
        @Override
        @MainThread
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            if (state == BluetoothProfile.STATE_DISCONNECTED) {
                finish();
            }
        }

        @Override
        @MainThread
        public void onGetReport(BluetoothDevice device, byte type, byte id, int bufferSize) {}

        @Override
        @MainThread
        public void onSetReport(BluetoothDevice device, byte type, byte id, byte[] data) {}

        @Override
        @MainThread
        public void onInterruptData(BluetoothDevice device, byte reportId, byte[] data) {
            if (reportId != Constants.ID_FEEDBACK) {
                return;
            }
            if (data == null || data.length == 0) {
                return;
            }
            if (data[0] == 0) {
                GamepadActivity.this.vibrateCancel();
                return;
            }
            if (data.length >= 8) {
                int magnitude = (data[1] & 0xFF + data[2] & 0xFF + data[3] & 0xFF + data[4] & 0xFF) / 4;
                int amplitude = (int) (255 * (magnitude / 100.0));
                int duration = (int) ((data[5] & 0xFF) * 10); // ms
                int startDelay = (int) ((data[6] & 0xFF) * 10); // ms
                int loopCount = (int) (data[7] & 0xFF);
                GamepadActivity.this.vibrateWaveform(amplitude, duration, startDelay, loopCount);
            } else {
                GamepadActivity.this.vibrateOneShot();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gamepad);

        hidDataSender = HidDataSender.getInstance();
        hidDataSender.register(this, profileListener);

        Button buttonA = findViewById(R.id.button_a);
        Button buttonB = findViewById(R.id.button_b);
        Button buttonX = findViewById(R.id.button_x);
        Button buttonY = findViewById(R.id.button_y);
        Button buttonL1 = findViewById(R.id.button_l1);
        Button buttonR1 = findViewById(R.id.button_r1);
        Button buttonL3 = findViewById(R.id.button_l3);
        Button buttonR3 = findViewById(R.id.button_r3);
        Button buttonView = findViewById(R.id.button_view);
        Button buttonMenu = findViewById(R.id.button_menu);
        Button buttonHome = findViewById(R.id.button_home);
        Button buttonRecord = findViewById(R.id.button_record);
        ImageView dPad = findViewById(R.id.dpad);
        ImageView stickLeft = findViewById(R.id.stick_left);
        ImageView stickRight = findViewById(R.id.stick_right);
        SeekBar seekbarL2 = findViewById(R.id.seekbar_l2);
        SeekBar seekbarR2 = findViewById(R.id.seekbar_r2);

        buttonA.setOnTouchListener(this::onTouchButton);
        buttonB.setOnTouchListener(this::onTouchButton);
        buttonX.setOnTouchListener(this::onTouchButton);
        buttonY.setOnTouchListener(this::onTouchButton);
        buttonL1.setOnTouchListener(this::onTouchButton);
        buttonR1.setOnTouchListener(this::onTouchButton);
        buttonL3.setOnTouchListener(this::onTouchButton);
        buttonR3.setOnTouchListener(this::onTouchButton);
        buttonView.setOnTouchListener(this::onTouchButton);
        buttonMenu.setOnTouchListener(this::onTouchButton);
        buttonHome.setOnTouchListener(this::onTouchButton);
        buttonRecord.setOnTouchListener(this::onTouchButton);

        dPad.setOnTouchListener(this::onTouchStick);
        stickLeft.setOnTouchListener(this::onTouchStick);
        stickRight.setOnTouchListener(this::onTouchStick);

        SeekBar.OnSeekBarChangeListener listener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int id = seekBar.getId();
                        if (id == R.id.seekbar_l2) {
                            gamepadState.l2 = progress;
                        } else if (id == R.id.seekbar_r2) {
                            gamepadState.r2 = progress;
                        }
                        send();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        GamepadActivity.this.vibrateOneShot();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekBar.setProgress(0);
                        send();
                    }
                };

        seekbarL2.setOnSeekBarChangeListener(listener);
        seekbarR2.setOnSeekBarChangeListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(android.R.id.content)
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hidDataSender.unregister(this, profileListener);
        if (hidVibrator != null) {
            hidVibrator.cancel();
        }
    }

    public void vibrateCancel() {
        if (hidVibrator != null) {
            hidVibrator.cancel();
        }
    }

    public void vibrateStart(VibrationEffect effect) {
        vibrateCancel();
        hidVibrator = (Vibrator)GamepadActivity.this.getSystemService(GamepadActivity.this.VIBRATOR_SERVICE);
        hidVibrator.vibrate(effect);
    }

    public void vibrateOneShot() {
        VibrationEffect effect = VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE);        hidVibrator.cancel();
        vibrateStart(effect);
    }

    public void vibrateWaveform(int amplitude, int duration, int startDelay, int loopCount) {
        if (amplitude <= 0 || duration <= 0 || startDelay < 0) {
            return;
        }
        int count = (loopCount + 1) * 2;
        long[] timings = new long[count];
        int[] amplitudes = new int[count];
        for (int index = 0; index < count; index++) {
            if (index % 2 == 0) {
                timings[index] = loopCount > 0 ? startDelay : duration;
                amplitudes[index] = loopCount > 0 ? 0 : amplitude;
            } else {
                timings[index] = duration;
                amplitudes[index] = amplitude;
            }
        }
        VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, -1);
        vibrateStart(effect);
    }

    public boolean onTouchButton(View v, MotionEvent event) {
        int action = event.getActionMasked();
        boolean state =
                !(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP);
        if (action == MotionEvent.ACTION_DOWN) {
            vibrateOneShot();
        }
        int id = v.getId();
        if (id == R.id.button_a) {
            gamepadState.a = state;
        } else if (id == R.id.button_b) {
            gamepadState.b = state;
        } else if (id == R.id.button_x) {
            gamepadState.x = state;
        } else if (id == R.id.button_y) {
            gamepadState.y = state;
        } else if (id == R.id.button_l1) {
            gamepadState.l1 = state;
        } else if (id == R.id.button_r1) {
            gamepadState.r1 = state;
        } else if (id == R.id.button_l3) {
            gamepadState.l3 = state;
        } else if (id == R.id.button_r3) {
            gamepadState.r3 = state;
        } else if (id == R.id.button_view) {
            gamepadState.view = state;
        } else if (id == R.id.button_menu) {
            gamepadState.menu = state;
        } else if (id == R.id.button_home) {
            gamepadState.home = state;
        } else if (id == R.id.button_record) {
            gamepadState.record = state;
        } else {
            return false;
        }
        send();
        return true;
    }

    public boolean onTouchStick(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int w = v.getMeasuredWidth();
        int h = v.getMeasuredHeight();
        float x = Math.min(Math.max(event.getX(), 0), w);
        float y = Math.min(Math.max(event.getY(), 0), h);

        boolean state =
                !(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP);
        if (action == MotionEvent.ACTION_DOWN) {
            vibrateOneShot();
        }
        int id = v.getId();
        if (id == R.id.dpad) {
            if (state) {
                int cx = w / 2;
                int cy = h / 2;
                float dx = x - cx;
                float dy = y - cy;
                double theta = Math.atan2(-dy, dx);
                if (theta < 0) {
                    theta += 2 * Math.PI;
                }
                int area = (int) (theta / (Math.PI / 8));
                gamepadState.dpad = eightWay[area];
            } else {
                gamepadState.dpad = 0;
            }
        } else if (id == R.id.stick_left) {
            if (state) {
                gamepadState.lx = Math.round(65535 * x / w);
                gamepadState.ly = Math.round(65535 * y / h);
            } else {
                gamepadState.lx = 32768;
                gamepadState.ly = 32768;
            }
        } else if (id == R.id.stick_right) {
            if (state) {
                gamepadState.rx = Math.round(65535 * x / w);
                gamepadState.ry = Math.round(65535 * y / h);
            } else {
                gamepadState.rx = 32768;
                gamepadState.ry = 32768;
            }
        } else {
            return false;
        }

        send();
        return true;
    }

    private void send() {
        hidDataSender.sendGamepad(gamepadState);
    }
}
