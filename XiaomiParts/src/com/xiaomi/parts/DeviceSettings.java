/*
 * Copyright (C) 2018 The Asus-SDM660 Project
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
 * limitations under the License
 */

package com.xiaomi.parts;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SELinux;
import android.os.Handler;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.SwitchPreference;
import android.util.Log;

import com.xiaomi.parts.kcal.KCalSettingsActivity;
import com.xiaomi.parts.speaker.ClearSpeakerActivity;
import com.xiaomi.parts.ambient.AmbientGesturePreferenceActivity;
import com.xiaomi.parts.preferences.CustomSeekBarPreference;
import com.xiaomi.parts.preferences.SecureSettingListPreference;
import com.xiaomi.parts.preferences.SecureSettingSwitchPreference;
import com.xiaomi.parts.preferences.VibratorStrengthPreference;
import com.xiaomi.parts.SuShell;
import com.xiaomi.parts.SuTask;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "XiaomiParts";

    public static final String PREF_CHARGING_LED = "charging_led";
    public static final String CHARGING_LED_PATH = "/sys/class/leds/charging/max_brightness";

    public static final String PREF_BACKLIGHT_DIMMER = "backlight_dimmer";
    public static final String BACKLIGHT_DIMMER_PATH = "/sys/module/mdss_fb/parameters/backlight_dimmer";

    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String CATEGORY_DISPLAY = "display";
    public static final String PREF_DEVICE_KCAL = "device_kcal";

    public static final String PREF_ENABLE_DIRAC = "dirac_enabled";
    public static final String PREF_HEADSET = "dirac_headset_pref";
    public static final String PREF_PRESET = "dirac_preset_pref";
    public static final String PREF_HEADPHONE_GAIN = "headphone_gain";
    public static final String HEADPHONE_GAIN_PATH = "/sys/kernel/sound_control/headphone_gain";
    public static final String PREF_MICROPHONE_GAIN = "microphone_gain";
    public static final String MICROPHONE_GAIN_PATH = "/sys/kernel/sound_control/mic_gain";
    public static final String PREF_EARPIECE_GAIN = "earpiece_gain";
    public static final String EARPIECE_GAIN_PATH = "/sys/kernel/sound_control/earpiece_gain";
    public static final String PREF_SPEAKER_GAIN = "speaker_gain";
    public static final String SPEAKER_GAIN_PATH = "/sys/kernel/sound_control/speaker_gain";
    public static final String CATEGORY_FASTCHARGE = "usb_fastcharge";
    public static final String PREF_USB_FASTCHARGE = "fastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";
    public static final String PREF_KEY_FPS_INFO = "fps_info";

    public static final String PREF_MSM_TOUCHBOOST = "touchboost";
    public static final String MSM_TOUCHBOOST_PATH = "/sys/module/msm_performance/parameters/touchboost";

    public static final String HIGH_PERF_AUDIO = "highperfaudio";
    public static final String HIGH_AUDIO_PATH = "/sys/module/snd_soc_wcd9330/parameters/high_perf_mode";

    public static final String PREF_LKM = "lkmprofile";
    public static final String LKM_SYSTEM_PROPERTY = "persist.lkm.profile";
    public static final String PREF_TCP = "tcpcongestion";
    public static final String TCP_SYSTEM_PROPERTY = "persist.tcp.profile";

    public static final String PREF_GPUBOOST = "gpuboost";
    public static final String GPUBOOST_SYSTEM_PROPERTY = "persist.gpuboost.profile";
    public static final String PREF_CPUBOOST = "cpuboost";
    public static final String CPUBOOST_SYSTEM_PROPERTY = "persist.cpuboost.profile";

    //public static final String PREF_USB_FASTCHARGE = "fastcharge";
    //public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    private CustomSeekBarPreference mTorchBrightness;

    private static final String SELINUX_CATEGORY = "selinux";
    private static final String PREF_SELINUX_MODE = "selinux_mode";
    private static final String PREF_SELINUX_PERSISTENCE = "selinux_persistence";

    private static final String PREF_CLEAR_SPEAKER = "clear_speaker_settings";

    private SecureSettingSwitchPreference mHighAudio;
    private SecureSettingSwitchPreference mMsmThermal;
    private SecureSettingSwitchPreference mCoreControl;
    private SecureSettingSwitchPreference mVddRestrict;
    private SecureSettingListPreference mCPUCORE;
    private SecureSettingListPreference mLKM;
    private SecureSettingListPreference mTCP;
    private VibratorStrengthPreference mVibratorStrength;
    private Preference mKcal;
    private Preference mAmbientPref;
    private SecureSettingSwitchPreference mEnableDirac;
    private SecureSettingListPreference mHeadsetType;
    private SecureSettingListPreference mPreset;
    private CustomSeekBarPreference mHeadphoneGain;
    private CustomSeekBarPreference mMicrophoneGain;
    private CustomSeekBarPreference mEarpieceGain;
    private CustomSeekBarPreference mSpeakerGain;

    private SecureSettingSwitchPreference mFastcharge;

    private SecureSettingSwitchPreference mBacklightDimmer;
    private SecureSettingSwitchPreference mTouchboost;
    private SecureSettingListPreference mGPUBOOST;
    private SecureSettingListPreference mCPUBOOST;
    private static Context mContext;
    private SwitchPreference mSelinuxMode;
    private SwitchPreference mSelinuxPersistence;
    private Preference mClearSpeakerPref;
    private Preference mAmbientPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_xiaomi_parts, rootKey);
        mContext = this.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String device = FileUtils.getStringProp("ro.build.product", "unknown");

        mClearSpeakerPref = (Preference) findPreference(PREF_CLEAR_SPEAKER);
        mClearSpeakerPref.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), ClearSpeakerActivity.class);
            startActivity(intent);
            return true;
        });


        PreferenceCategory displayCategory = (PreferenceCategory) findPreference(CATEGORY_DISPLAY);

        mKcal = findPreference(PREF_DEVICE_KCAL);

        mKcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mAmbientPref = findPreference("ambient_display_gestures");
        mAmbientPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), AmbientGesturePreferenceActivity.class);
                startActivity(intent);
                return true;
            }
        });


        if (FileUtils.fileWritable(BACKLIGHT_DIMMER_PATH)) {
            mBacklightDimmer = (SecureSettingSwitchPreference) findPreference(PREF_BACKLIGHT_DIMMER);
            mBacklightDimmer.setEnabled(BacklightDimmer.isSupported());
            mBacklightDimmer.setChecked(BacklightDimmer.isCurrentlyEnabled(this.getContext()));
            mBacklightDimmer.setOnPreferenceChangeListener(new BacklightDimmer(getContext()));
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_BACKLIGHT_DIMMER));
        }

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        boolean enhancerEnabled;
        try {
            enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
        } catch (java.lang.NullPointerException e) {
            getContext().startService(new Intent(getContext(), DiracService.class));
            try {
                enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
            } catch (NullPointerException ne) {
                // Avoid crash
                ne.printStackTrace();
                enhancerEnabled = false;
            }
        }

        mEnableDirac = (SecureSettingSwitchPreference) findPreference(PREF_ENABLE_DIRAC);
        mEnableDirac.setOnPreferenceChangeListener(this);
        mEnableDirac.setChecked(enhancerEnabled);

        mHeadsetType = (SecureSettingListPreference) findPreference(PREF_HEADSET);
        mHeadsetType.setOnPreferenceChangeListener(this);

        mPreset = (SecureSettingListPreference) findPreference(PREF_PRESET);
        mPreset.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(HIGH_AUDIO_PATH)) {
            mHighAudio = (SecureSettingSwitchPreference) findPreference(HIGH_PERF_AUDIO);
            mHighAudio.setChecked(FileUtils.getFileValueAsBoolean(HIGH_AUDIO_PATH, true));
            mHighAudio.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(HIGH_PERF_AUDIO));
        }

        mHeadphoneGain = (CustomSeekBarPreference) findPreference(PREF_HEADPHONE_GAIN);
        mHeadphoneGain.setOnPreferenceChangeListener(this);

        mMicrophoneGain = (CustomSeekBarPreference) findPreference(PREF_MICROPHONE_GAIN);
        mMicrophoneGain.setOnPreferenceChangeListener(this);

        mEarpieceGain = (CustomSeekBarPreference) findPreference(PREF_EARPIECE_GAIN);
        mEarpieceGain.setOnPreferenceChangeListener(this);

        mSpeakerGain = (CustomSeekBarPreference) findPreference(PREF_SPEAKER_GAIN);
        mSpeakerGain.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(USB_FASTCHARGE_PATH)) {
            mFastcharge = (SecureSettingSwitchPreference) findPreference(PREF_USB_FASTCHARGE);
            mFastcharge.setEnabled(Fastcharge.isSupported());
            mFastcharge.setChecked(Fastcharge.isCurrentlyEnabled(this.getContext()));
            mFastcharge.setOnPreferenceChangeListener(new Fastcharge(getContext()));
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_FASTCHARGE));
        }

        if (FileUtils.fileWritable(MSM_TOUCHBOOST_PATH)) {
            mTouchboost = (SecureSettingSwitchPreference) findPreference(PREF_MSM_TOUCHBOOST);
            mTouchboost.setChecked(FileUtils.getFileValueAsBoolean(MSM_TOUCHBOOST_PATH, true));
            mTouchboost.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_MSM_TOUCHBOOST));
        }

        mGPUBOOST = (SecureSettingListPreference) findPreference(PREF_GPUBOOST);
        mGPUBOOST.setValue(FileUtils.getStringProp(GPUBOOST_SYSTEM_PROPERTY, "0"));
        mGPUBOOST.setSummary(mGPUBOOST.getEntry());
        mGPUBOOST.setOnPreferenceChangeListener(this);

        mCPUBOOST = (SecureSettingListPreference) findPreference(PREF_CPUBOOST);
        mCPUBOOST.setValue(FileUtils.getStringProp(CPUBOOST_SYSTEM_PROPERTY, "0"));
        mCPUBOOST.setSummary(mCPUBOOST.getEntry());
        mCPUBOOST.setOnPreferenceChangeListener(this);

        mLKM = (SecureSettingListPreference) findPreference(PREF_LKM);
        mLKM.setValue(FileUtils.getStringProp(LKM_SYSTEM_PROPERTY, "0"));
        mLKM.setSummary(mLKM.getEntry());
        mLKM.setOnPreferenceChangeListener(this);

        mTCP = (SecureSettingListPreference) findPreference(PREF_TCP);
        mTCP.setValue(FileUtils.getStringProp(TCP_SYSTEM_PROPERTY, "0"));
        mTCP.setSummary(mTCP.getEntry());
        mTCP.setOnPreferenceChangeListener(this);

        SwitchPreference fpsInfo = (SwitchPreference) findPreference(PREF_KEY_FPS_INFO);
        fpsInfo.setChecked(prefs.getBoolean(PREF_KEY_FPS_INFO, false));
        fpsInfo.setOnPreferenceChangeListener(this);

        // SELinux
        Preference selinuxCategory = findPreference(SELINUX_CATEGORY);
        mSelinuxMode = (SwitchPreference) findPreference(PREF_SELINUX_MODE);
        mSelinuxMode.setChecked(SELinux.isSELinuxEnforced());
        mSelinuxMode.setOnPreferenceChangeListener(this);

        mSelinuxPersistence =
        (SwitchPreference) findPreference(PREF_SELINUX_PERSISTENCE);
        mSelinuxPersistence.setOnPreferenceChangeListener(this);
        mSelinuxPersistence.setChecked(getContext()
        .getSharedPreferences("selinux_pref", Context.MODE_PRIVATE)
        .contains(PREF_SELINUX_MODE));
  }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case PREF_LKM:
                mLKM.setValue((String) value);
                mLKM.setSummary(mLKM.getEntry());
                FileUtils.setStringProp(LKM_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_TCP:
                mTCP.setValue((String) value);
                mTCP.setSummary(mTCP.getEntry());
                FileUtils.setStringProp(TCP_SYSTEM_PROPERTY, (String) value);
                break;

            case HIGH_PERF_AUDIO:
                FileUtils.setValue(HIGH_AUDIO_PATH, (boolean) value);
                break;

            case PREF_ENABLE_DIRAC:
                try {
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                }
                break;

            case PREF_HEADSET:
                try {
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                }
                break;

            case PREF_PRESET:
                try {
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                }
                break;

            case PREF_HEADPHONE_GAIN:
                FileUtils.setValue(HEADPHONE_GAIN_PATH, value + " " + value);
                break;

            case PREF_MICROPHONE_GAIN:
                FileUtils.setValue(MICROPHONE_GAIN_PATH, (int) value);
                break;

            case PREF_EARPIECE_GAIN:
                FileUtils.setValue(EARPIECE_GAIN_PATH, (int) value);
                break;

            case PREF_SPEAKER_GAIN:
                 FileUtils.setValue(SPEAKER_GAIN_PATH, (int) value);
                break;

            case PREF_GPUBOOST:
                mGPUBOOST.setValue((String) value);
                mGPUBOOST.setSummary(mGPUBOOST.getEntry());
                FileUtils.setStringProp(GPUBOOST_SYSTEM_PROPERTY, (String) value);
		break;
		
            case PREF_USB_FASTCHARGE:
                FileUtils.setValue(USB_FASTCHARGE_PATH, (boolean) value);
                break;

            case PREF_CPUBOOST:
                mCPUBOOST.setValue((String) value);
                mCPUBOOST.setSummary(mCPUBOOST.getEntry());
                FileUtils.setStringProp(CPUBOOST_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_SELINUX_MODE:
                  if (preference == mSelinuxMode) {
                  boolean enabled = (Boolean) value;
                  new SwitchSelinuxTask(getActivity()).execute(enabled);
                  setSelinuxEnabled(enabled, mSelinuxPersistence.isChecked());
                  return true;
                } else if (preference == mSelinuxPersistence) {
                  setSelinuxEnabled(mSelinuxMode.isChecked(), (Boolean) value);
                  return true;
                }
                break;

            case PREF_KEY_FPS_INFO:
                boolean enabled = (Boolean) value;
                Intent fpsinfo = new Intent(this.getContext(), FPSInfoService.class);
                if (enabled) {
                    this.getContext().startService(fpsinfo);
                } else {
                    this.getContext().stopService(fpsinfo);
                }
                break;
            default:
                break;
        }
        return true;
    }

        private void setSelinuxEnabled(boolean status, boolean persistent) {
          SharedPreferences.Editor editor = getContext()
              .getSharedPreferences("selinux_pref", Context.MODE_PRIVATE).edit();
          if (persistent) {
            editor.putBoolean(PREF_SELINUX_MODE, status);
          } else {
            editor.remove(PREF_SELINUX_MODE);
          }
          editor.apply();
          mSelinuxMode.setChecked(status);
        }

        private class SwitchSelinuxTask extends SuTask<Boolean> {
          public SwitchSelinuxTask(Context context) {
            super(context);
          }
          @Override
          protected void sudoInBackground(Boolean... params) throws SuShell.SuDeniedException {
            if (params.length != 1) {
              Log.e(TAG, "SwitchSelinuxTask: invalid params count");
              return;
            }
            if (params[0]) {
              SuShell.runWithSuCheck("setenforce 1");
            } else {
              SuShell.runWithSuCheck("setenforce 0");
            }
          }

          @Override
          protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
              // Did not work, so restore actual value
              setSelinuxEnabled(SELinux.isSELinuxEnforced(), mSelinuxPersistence.isChecked());
            }
          }
        }

    private boolean isAppNotInstalled(String uri) {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
