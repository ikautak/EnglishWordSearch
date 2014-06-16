package com.exiashio.EijiroSearch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class EnglishWordSearchActivity extends Activity {
    private static final boolean DEBUG = false;
    private static final String TAG = "EnglishWordSearch";

    private Context mContext;

    // view
    private EditText mSearchWordInput;
    private ImageButton mSearchButton;
    private ImageButton mVoiceButton;

    // request code
    private static final int REQUEST_VOICE = 1;

    // option menu
    private static final int MENU_ID_PREFERENCE = Menu.FIRST + 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.main);

        // EditText
        mSearchWordInput = (EditText)findViewById(R.id.edit_text);
        mSearchWordInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    search();
                    return true;
                }
                return false;
            }
        });

        // SearchButton
        mSearchButton = (ImageButton)findViewById(R.id.button_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        // VoiceButton
        mVoiceButton = (ImageButton)findViewById(R.id.button_voice);

        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            mVoiceButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.app_name);

                    if (Preference.isVoiceEnglish(mContext)) {
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                    }
                    startActivityForResult(intent, REQUEST_VOICE);
                }
            });
        } else {
            mVoiceButton.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Try to open IME
        if (Preference.isAutoIme(this)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(mSearchWordInput, 0);
                }

            }, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VOICE) {
                onVoiceResult(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_PREFERENCE, Menu.NONE, R.string.menu_setting);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;

        Log.d(TAG, "item.getItemId" + item.getItemId());
        switch (item.getItemId()) {
        case MENU_ID_PREFERENCE:
            startPreference();
            ret = true;
            break;

        default:
            ret = super.onOptionsItemSelected(item);
            break;
        }

        return ret;
    }

    private void startPreference() {
        Intent intent = new Intent(this, Preference.class);
        startActivity(intent);
    }

    private void search() {
        // get search keyword
        String word = mSearchWordInput.getText().toString();
        if (word.length() <= 0) {
            return;
        }

        // search ALC
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                .parse("http://eow.alc.co.jp/sp/search.html?q=" + word + "&pg=1"));
        startActivity(intent);

        finish();
    }

    private void onVoiceResult(Intent data) {
        ArrayList<String> matches = data.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS);

        // set search word
        mSearchWordInput.setText(matches.get(0));
    }
}
