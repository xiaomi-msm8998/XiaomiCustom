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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

import org.lineageos.settings.device.R;
import org.lineageos.settings.device.utils.FileUtils;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_CATEGORY_HW_BUTTONS = "hw_buttons";
    private static final String KEY_CATEGORY_DISPLAY = "display";
    private static final String SPECTRUM_KEY = "spectrum";
    private static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";
    private static final String KEY_CATEGORY_USB_FASTCHARGE = "usb_fastcharge";
    public static final String KEY_VIBSTRENGTH = "vib_strength";

    public static final String BUTTONS_SWAP_KEY = "buttons_swap";
    public static final String BUTTONS_SWAP_PATH = "/proc/touchpanel/reversed_keys_enable";

    public static final String USB_FASTCHARGE_KEY = "fastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    final String KEY_DEVICE_DOZE = "device_doze";
    final String KEY_DEVICE_DOZE_PACKAGE_NAME = "org.lineageos.settings.doze";

    private TwoStatePreference mTapToWakeSwitch;
    private VibratorStrengthPreference mVibratorStrength;

    private Preference mKcalPref;
    private ListPreference mSPECTRUM;
    private SwitchPreference mButtonSwap;
    private PreferenceCategory mHWButtons;
    private SwitchPreference mFastcharge;
    private PreferenceCategory mUsbFastcharge;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        PreferenceScreen prefSet = getPreferenceScreen();

        mKcalPref = findPreference("kcal");
        mKcalPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), DisplayCalibration.class);
                startActivity(intent);
                return true;
            }
        });

        if (FileUtils.isFileWritable(BUTTONS_SWAP_PATH)) {
            mButtonSwap = (SwitchPreference) findPreference(BUTTONS_SWAP_KEY);
            mButtonSwap.setChecked(FileUtils.getFileValueAsBoolean(BUTTONS_SWAP_PATH, false));
            mButtonSwap.setOnPreferenceChangeListener(this);
        } else {
            mHWButtons = (PreferenceCategory) prefSet.findPreference("hw_buttons");
            prefSet.removePreference(mHWButtons);
        }

        if (FileUtils.isFileWritable(USB_FASTCHARGE_PATH)) {
          mFastcharge = (SwitchPreference) findPreference(USB_FASTCHARGE_KEY);
          mFastcharge.setChecked(FileUtils.getFileValueAsBoolean(USB_FASTCHARGE_PATH, false));
          mFastcharge.setOnPreferenceChangeListener(this);
        } else {
          mUsbFastcharge = (PreferenceCategory) prefSet.findPreference("usb_fastcharge");
          prefSet.removePreference(mUsbFastcharge);
        }

        mSPECTRUM = (ListPreference) findPreference(SPECTRUM_KEY);
        if( mSPECTRUM != null ) {
            mSPECTRUM.setValue(SystemProperties.get(SPECTRUM_SYSTEM_PROPERTY, "0"));
            mSPECTRUM.setOnPreferenceChangeListener(this);
        }

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        if (!isAppInstalled(KEY_DEVICE_DOZE_PACKAGE_NAME)) {
            PreferenceCategory displayCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_DISPLAY);
            displayCategory.removePreference(findPreference(KEY_DEVICE_DOZE));
        }
    }

    private void setButtonSwap(boolean value) {
        FileUtils.writeValue(BUTTONS_SWAP_PATH, value ? "1" : "0");
    }

    private void setFastcharge(boolean value) {
        FileUtils.writeValue(USB_FASTCHARGE_PATH, value ? "1" : "0");
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        boolean value;
        String strvalue;
        if (SPECTRUM_KEY.equals(key)) {
            strvalue = (String) newValue;
            SystemProperties.set(SPECTRUM_SYSTEM_PROPERTY, strvalue);
            return true;
        } else if (BUTTONS_SWAP_KEY.equals(key)) {
            value = (Boolean) newValue;
            mButtonSwap.setChecked(value);
            setButtonSwap(value);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putBoolean(BUTTONS_SWAP_KEY, value);
            editor.commit();
            return true;
        } else if (USB_FASTCHARGE_KEY.equals(key)) {
            value = (Boolean) newValue;
            mFastcharge.setChecked(value);
            setFastcharge(value);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putBoolean(USB_FASTCHARGE_KEY, value);
            editor.commit();
            return true;
        }
        return true;
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
