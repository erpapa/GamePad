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

import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;

/** Handy constants that are shared between Classic and BLE modes. */
class Constants {
    static final byte ID_GAMEPAD = 1;
    static final byte ID_FEEDBACK = 3;
    static final byte ID_BATTERY = 4;

    // Xbox Wireless Controller (Xbox model 1914)
    private static final byte[] HIDD_REPORT_DESC = {
            (byte) 0x05, (byte) 0x01,        // Usage Page (Generic Desktop Ctrls)
            (byte) 0x09, (byte) 0x05,        // Usage (Game Pad)
            (byte) 0xA1, (byte) 0x01,        // Collection (Application)
            (byte) 0x85, ID_GAMEPAD,         //   Report ID (1)
            (byte) 0x09, (byte) 0x01,        //   Usage (Pointer)
            (byte) 0xA1, (byte) 0x00,        //   Collection (Physical)
            (byte) 0x09, (byte) 0x30,        //     Usage (X)
            (byte) 0x09, (byte) 0x31,        //     Usage (Y)
            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
            (byte) 0x27, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00,  //     Logical Maximum (65535)
            (byte) 0x95, (byte) 0x02,        //     Report Count (2)
            (byte) 0x75, (byte) 0x10,        //     Report Size (16)
            (byte) 0x81, (byte) 0x02,        //     Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0xC0,                     //   End Collection
            (byte) 0x09, (byte) 0x01,        //   Usage (Pointer)
            (byte) 0xA1, (byte) 0x00,        //   Collection (Physical)
            (byte) 0x09, (byte) 0x32,        //     Usage (Z)
            (byte) 0x09, (byte) 0x35,        //     Usage (Rz)
            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
            (byte) 0x27, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00,  //     Logical Maximum (65535)
            (byte) 0x95, (byte) 0x02,        //     Report Count (2)
            (byte) 0x75, (byte) 0x10,        //     Report Size (16)
            (byte) 0x81, (byte) 0x02,        //     Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0xC0,                     //   End Collection
            (byte) 0x05, (byte) 0x02,        //   Usage Page (Sim Ctrls)
            (byte) 0x09, (byte) 0xC5,        //   Usage (Brake)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x26, (byte) 0xFF, (byte) 0x03,  //   Logical Maximum (1023)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x75, (byte) 0x0A,        //   Report Size (10)
            (byte) 0x81, (byte) 0x02,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x00,        //   Logical Maximum (0)
            (byte) 0x75, (byte) 0x06,        //   Report Size (6)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x03,        //   Input (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x05, (byte) 0x02,        //   Usage Page (Sim Ctrls)
            (byte) 0x09, (byte) 0xC4,        //   Usage (Accelerator)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x26, (byte) 0xFF, (byte) 0x03,  //   Logical Maximum (1023)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x75, (byte) 0x0A,        //   Report Size (10)
            (byte) 0x81, (byte) 0x02,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x00,        //   Logical Maximum (0)
            (byte) 0x75, (byte) 0x06,        //   Report Size (6)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x03,        //   Input (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x05, (byte) 0x01,        //   Usage Page (Generic Desktop Ctrls)
            (byte) 0x09, (byte) 0x39,        //   Usage (Hat switch)
            (byte) 0x15, (byte) 0x01,        //   Logical Minimum (1)
            (byte) 0x25, (byte) 0x08,        //   Logical Maximum (8)
            (byte) 0x35, (byte) 0x00,        //   Physical Minimum (0)
            (byte) 0x46, (byte) 0x3B, (byte) 0x01,  //   Physical Maximum (315)
            (byte) 0x66, (byte) 0x14, (byte) 0x00,  //   Unit (System: English Rotation, Length: Centimeter)
            (byte) 0x75, (byte) 0x04,        //   Report Size (4)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x42,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,Null State)
            (byte) 0x75, (byte) 0x04,        //   Report Size (4)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x00,        //   Logical Maximum (0)
            (byte) 0x35, (byte) 0x00,        //   Physical Minimum (0)
            (byte) 0x45, (byte) 0x00,        //   Physical Maximum (0)
            (byte) 0x65, (byte) 0x00,        //   Unit (None)
            (byte) 0x81, (byte) 0x03,        //   Input (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x05, (byte) 0x09,        //   Usage Page (Button)
            (byte) 0x19, (byte) 0x01,        //   Usage Minimum ((byte) 0x01)
            (byte) 0x29, (byte) 0x0F,        //   Usage Maximum ((byte) 0x0F)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x01,        //   Logical Maximum (1)
            (byte) 0x75, (byte) 0x01,        //   Report Size (1)
            (byte) 0x95, (byte) 0x0F,        //   Report Count (15)
            (byte) 0x81, (byte) 0x02,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x00,        //   Logical Maximum (0)
            (byte) 0x75, (byte) 0x01,        //   Report Size (1)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x03,        //   Input (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x05, (byte) 0x0C,        //   Usage Page (Consumer)
            (byte) 0x0A, (byte) 0xB2, (byte) 0x00,  //   Usage (Record)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x01,        //   Logical Maximum (1)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x75, (byte) 0x01,        //   Report Size (1)
            (byte) 0x81, (byte) 0x02,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x25, (byte) 0x00,        //   Logical Maximum (0)
            (byte) 0x75, (byte) 0x07,        //   Report Size (7)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x03,        //   Input (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)

//            // Force feedback and related devices
//            (byte) 0x05, (byte) 0x0F,        //   Usage Page (PID Page)
//            (byte) 0x09, (byte) 0x21,        //   Usage ((byte) 0x21)
//            (byte) 0x85, ID_FEEDBACK,        //   Report ID (3)
//            (byte) 0xA1, (byte) 0x02,        //   Collection (Logical)
//            (byte) 0x09, (byte) 0x97,        //     Usage ((byte) 0x97)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x25, (byte) 0x01,        //     Logical Maximum (1)
//            (byte) 0x75, (byte) 0x04,        //     Report Size (4)
//            (byte) 0x95, (byte) 0x01,        //     Report Count (1)
//            (byte) 0x91, (byte) 0x02,        //     Output (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x25, (byte) 0x00,        //     Logical Maximum (0)
//            (byte) 0x75, (byte) 0x04,        //     Report Size (4)
//            (byte) 0x95, (byte) 0x01,        //     Report Count (1)
//            (byte) 0x91, (byte) 0x03,        //     Output (Const,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0x09, (byte) 0x70,        //     Usage ((byte) 0x70)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x25, (byte) 0x64,        //     Logical Maximum (100)
//            (byte) 0x75, (byte) 0x08,        //     Report Size (8)
//            (byte) 0x95, (byte) 0x04,        //     Report Count (4)
//            (byte) 0x91, (byte) 0x02,        //     Output (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0x09, (byte) 0x50,        //     Usage ((byte) 0x50)
//            (byte) 0x66, (byte) 0x01, (byte) 0x10,  //     Unit (System: SI Linear, Time: Seconds)
//            (byte) 0x55, (byte) 0x0E,        //     Unit Exponent (-2)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x26, (byte) 0xFF, (byte) 0x00,  //     Logical Maximum (255)
//            (byte) 0x75, (byte) 0x08,        //     Report Size (8)
//            (byte) 0x95, (byte) 0x01,        //     Report Count (1)
//            (byte) 0x91, (byte) 0x02,        //     Output (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0x09, (byte) 0xA7,        //     Usage ((byte) 0xA7)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x26, (byte) 0xFF, (byte) 0x00,  //     Logical Maximum (255)
//            (byte) 0x75, (byte) 0x08,        //     Report Size (8)
//            (byte) 0x95, (byte) 0x01,        //     Report Count (1)
//            (byte) 0x91, (byte) 0x02,        //     Output (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0x65, (byte) 0x00,        //     Unit (None)
//            (byte) 0x55, (byte) 0x00,        //     Unit Exponent (0)
//            (byte) 0x09, (byte) 0x7C,        //     Usage ((byte) 0x7C)
//            (byte) 0x15, (byte) 0x00,        //     Logical Minimum (0)
//            (byte) 0x26, (byte) 0xFF, (byte) 0x00,  //     Logical Maximum (255)
//            (byte) 0x75, (byte) 0x08,        //     Report Size (8)
//            (byte) 0x95, (byte) 0x01,        //     Report Count (1)
//            (byte) 0x91, (byte) 0x02,        //     Output (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position,Non-volatile)
//            (byte) 0xC0,                     //   End Collection

            // Battery level, 1 byte, 0-FF
            (byte) 0x05, (byte) 0x06,        //   Usage Page (Generic Dev Ctrls)
            (byte) 0x09, (byte) 0x20,        //   Usage (Battery Strength)
            (byte) 0x85, ID_BATTERY,         //   Report ID
            (byte) 0x15, (byte) 0x00,        //   Logical Minimum (0)
            (byte) 0x26, (byte) 0xFF, (byte) 0x00, //   Logical Maximum (255)
            (byte) 0x75, (byte) 0x08,        //   Report Size (8)
            (byte) 0x95, (byte) 0x01,        //   Report Count (1)
            (byte) 0x81, (byte) 0x02,        //   Input (Data,Var,Abs,No Wrap,Linear,Preferred State,No Null Position)
            (byte) 0xC0,                     // End Collection
    };

    /**
     * typedef struct
     * {
     *   uint8_t  reportId;                                 // Report ID = 0x01 (1)
     *                                                      // Collection: CA:GamePad CP:Pointer
     *   uint16_t GD_GamePadPointerX;                       // Usage 0x00010030: X, Value = 0 to 65535
     *   uint16_t GD_GamePadPointerY;                       // Usage 0x00010031: Y, Value = 0 to 65535
     *   uint16_t GD_GamePadPointerZ;                       // Usage 0x00010032: Z, Value = 0 to 65535
     *   uint16_t GD_GamePadPointerRz;                      // Usage 0x00010035: Rz, Value = 0 to 65535
     *                                                      // Collection: CA:GamePad
     *   uint16_t SIM_GamePadBrake : 10;                    // Usage 0x000200C5: Brake, Value = 0 to 1023
     *   uint8_t  : 6;                                      // Pad
     *   uint16_t SIM_GamePadAccelerator : 10;              // Usage 0x000200C4: Accelerator, Value = 0 to 1023
     *   uint8_t  : 6;                                      // Pad
     *   uint8_t  GD_GamePadHatSwitch : 4;                  // Usage 0x00010039: Hat switch, Value = 1 to 8, Physical = (Value - 1) x 45 in degrees
     *   uint8_t  : 4;                                      // Pad
     *   uint8_t  BTN_GamePadButton1 : 1;                   // Usage 0x00090001: Button 1 Primary/trigger, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton2 : 1;                   // Usage 0x00090002: Button 2 Secondary, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton3 : 1;                   // Usage 0x00090003: Button 3 Tertiary, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton4 : 1;                   // Usage 0x00090004: Button 4, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton5 : 1;                   // Usage 0x00090005: Button 5, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton6 : 1;                   // Usage 0x00090006: Button 6, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton7 : 1;                   // Usage 0x00090007: Button 7, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton8 : 1;                   // Usage 0x00090008: Button 8, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton9 : 1;                   // Usage 0x00090009: Button 9, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton10 : 1;                  // Usage 0x0009000A: Button 10, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton11 : 1;                  // Usage 0x0009000B: Button 11, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton12 : 1;                  // Usage 0x0009000C: Button 12, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton13 : 1;                  // Usage 0x0009000D: Button 13, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton14 : 1;                  // Usage 0x0009000E: Button 14, Value = 0 to 1
     *   uint8_t  BTN_GamePadButton15 : 1;                  // Usage 0x0009000F: Button 15, Value = 0 to 1
     *   uint8_t  : 1;                                      // Pad
     *   uint8_t  CD_GamePadRecord : 1;                     // Usage 0x000C00B2: Record, Value = 0 to 1
     *   uint8_t  : 7;                                      // Pad
     * } inputReport01_t;
     *
     * typedef struct
     * {
     *   uint8_t  reportId;                                 // Report ID = 0x03 (3)
     *                                                      // Collection: CA:GamePad CL:SetEffectReport
     *   uint8_t  PID_GamePadSetEffectReportDcEnableActuators : 4; // Usage 0x000F0097: DC Enable Actuators, Value = 0 to 1
     *   uint8_t  : 4;                                      // Pad
     *   uint8_t  PID_GamePadSetEffectReportMagnitude[4];   // Usage 0x000F0070: Magnitude, Value = 0 to 100
     *   uint8_t  PID_GamePadSetEffectReportDuration;       // Usage 0x000F0050: Duration, Value = 0 to 255, Physical = Value in 10⁻² s units
     *   uint8_t  PID_GamePadSetEffectReportStartDelay;     // Usage 0x000F00A7: Start Delay, Value = 0 to 255, Physical = Value in 10⁻² s units
     *   uint8_t  PID_GamePadSetEffectReportLoopCount;      // Usage 0x000F007C: Loop Count, Value = 0 to 255
     * } outputReport03_t;
     */

    private static final String SDP_NAME = "Android Gamepad";
    private static final String SDP_DESCRIPTION = "Android HID Device";
    private static final String SDP_PROVIDER = "Google Inc.";
    private static final int QOS_TOKEN_RATE = 800; // 9 bytes * 1000000 us / 11250 us
    private static final int QOS_TOKEN_BUCKET_SIZE = 9;
    private static final int QOS_PEAK_BANDWIDTH = 0;
    private static final int QOS_LATENCY = 11250;

    static final BluetoothHidDeviceAppSdpSettings SDP_SETTINGS =
            new BluetoothHidDeviceAppSdpSettings(
                    SDP_NAME,
                    SDP_DESCRIPTION,
                    SDP_PROVIDER,
                    BluetoothHidDevice.SUBCLASS2_GAMEPAD,
                    HIDD_REPORT_DESC);

    static final BluetoothHidDeviceAppQosSettings QOS_SETTINGS =
            new BluetoothHidDeviceAppQosSettings(
                    BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                    QOS_TOKEN_RATE,
                    QOS_TOKEN_BUCKET_SIZE,
                    QOS_PEAK_BANDWIDTH,
                    QOS_LATENCY,
                    BluetoothHidDeviceAppQosSettings.MAX);
}
