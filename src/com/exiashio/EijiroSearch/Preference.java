package com.exiashio.EijiroSearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class Preference extends Activity {
    // shared preference
    private static final String AUTO_IME_CHECK = "auto_ime_checkbox";
    private static final String VOICE_ENGLISH_CHECK = "voice_english_checkbox";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
        .replace(android.R.id.content,  new PrefFragment()).commit();
    }

    public static class PrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
    }

    public static boolean isAutoIme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(AUTO_IME_CHECK, false);
    }

    public static boolean isVoiceEnglish(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(VOICE_ENGLISH_CHECK, false);
    }
}