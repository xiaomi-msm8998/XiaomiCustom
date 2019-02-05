/*
 * Copyright (c) 2015 The CyanogenMod Project
 *               2017 The LineageOS Project
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
package org.lineageos.settings.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;

public class Startup extends BroadcastReceiver {

    private void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        if (enabled) {
            FileUtils.writeValue(file, "1");
        }
    }

    private void restore(String file, String value) {
        if (file == null) {
            return;
        }
        FileUtils.writeValue(file, value);
    }

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        VibratorStrengthPreference.restore(context);
        boolean btnSwapStoredValue = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.BUTTONS_SWAP_KEY, false);
        FileUtils.writeValue(DeviceSettings.BUTTONS_SWAP_PATH, btnSwapStoredValue ? "1" : "0");
        boolean usbFastchargeStoredValue = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.USB_FASTCHARGE_KEY, false);
        FileUtils.writeValue(DeviceSettings.USB_FASTCHARGE_PATH, usbFastchargeStoredValue ? "1" : "0" );
        DisplayCalibration.restore(context);
    }
}
