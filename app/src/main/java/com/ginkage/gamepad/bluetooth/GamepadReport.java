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

import java.util.Arrays;

/** Helper class to store the gamepad state and retrieve the binary report. */
class GamepadReport {
    private final byte[] gamepadData = {
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0,
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0,
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0,
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0,
    };

    GamepadReport() {
        Arrays.fill(gamepadData, (byte) 0x0);
    }

    /**
     * Convert the state structure to the binary representation.
     *
     * @param s The gamepad state to serialize
     */
    byte[] setValue(GamepadState s) {
        // Pointer (x,y,x,rz) 4 x 16-bit
        // Brake 10-bit + 6-bit
        // Accelerator 10-bit + 6-bit
        // HatSwitch 4-bit + 4-bit
        // Buttons (A, B, X, Y, L1, R1, L2, R2, View, Menu, L3, R3, Up, Down, Left, Right, home) 15-bit + 1-bit
        // Record 1-bit + 7-bit

        gamepadData[0] = (byte) (s.lx & 0xFF);
        gamepadData[1] = (byte) ((s.lx & 0xFF00) >> 8);
        gamepadData[2] = (byte) (s.ly & 0xFF);
        gamepadData[3] = (byte) ((s.ly & 0xFF00) >> 8);
        gamepadData[4] = (byte) (s.rx & 0xFF);
        gamepadData[5] = (byte) ((s.rx & 0xFF00) >> 8);
        gamepadData[6] = (byte) (s.ry & 0xFF);
        gamepadData[7] = (byte) ((s.ry & 0xFF00) >> 8);
        gamepadData[8] = (byte) (s.l2 & 0xFF);
        gamepadData[9] = (byte) ((s.l2 & 0xFF00) >> 8);
        gamepadData[10] = (byte) (s.r2 & 0xFF);
        gamepadData[11] = (byte) ((s.r2 & 0xFF00) >> 8);
        gamepadData[12] = (byte) (s.dpad & 0xFF);

        gamepadData[13] = 0;
        gamepadData[13] |= (byte) (s.a ? 0x01 : 0);
        gamepadData[13] |= (byte) (s.b ? 0x02 : 0);
        gamepadData[13] |= (byte) (s.x ? 0x04 : 0);
        gamepadData[13] |= (byte) (s.y ? 0x08 : 0);
        gamepadData[13] |= (byte) (s.l1 ? 0x10 : 0);
        gamepadData[13] |= (byte) (s.r1 ? 0x20 : 0);
        // gamepadData[13] |= (byte) (s.l2 ? 0x40 : 0);
        // gamepadData[13] |= (byte) (s.r2 ? 0x80 : 0);

        gamepadData[14] = 0;
        gamepadData[14] |= (byte) (s.view ? 0x01 : 0);
        gamepadData[14] |= (byte) (s.menu ? 0x02 : 0);
        gamepadData[14] |= (byte) (s.l3 ? 0x04 : 0);
        gamepadData[14] |= (byte) (s.r3 ? 0x08 : 0);
        gamepadData[14] |= (byte) (s.home ? 0x40 : 0);

        gamepadData[15] = 0;
        gamepadData[15] |= (byte) (s.record ? 0x01 : 0);

        return gamepadData;
    }

    byte[] getReport() {
    return gamepadData;
  }

    /** Interface to send the Mouse data with. */
    public interface GamepadDataSender {
        /**
         * Send the Gamepad data to the connected HID Host device.
         *
         * @param state The current state of the gamepad.
         */
        void sendGamepad(GamepadState state);
    }
}
