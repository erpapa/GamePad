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

/** The gamepad state structure. */
public class GamepadState {
    public boolean a;
    public boolean b;
    public boolean x;
    public boolean y;
    public boolean l1;
    public boolean r1;
    public boolean l3;
    public boolean r3;
    public boolean view;
    public boolean menu;
    public boolean home;
    public boolean record;

    // 1=up, 3=right, 5=down, 7=left, 0=release
    public int dpad;

    // Sticks: Up=0, Down=65535, Left=0, Right=65535, Center=32768
    public int lx;
    public int ly;
    public int rx;
    public int ry;

    // Triggers: Released=0, Pressed=1023
    public int l2;
    public int r2;

    public GamepadState() {
        a = false;
        b = false;
        x = false;
        y = false;
        l1 = false;
        r1 = false;
        l3 = false;
        r3 = false;
        view = false;
        menu = false;
        home = false;
        record = false;
        dpad = 0;
        lx = 32768;
        ly = 32768;
        rx = 32768;
        ry = 32768;
        l2 = 0;
        r2 = 0;
    }
}
