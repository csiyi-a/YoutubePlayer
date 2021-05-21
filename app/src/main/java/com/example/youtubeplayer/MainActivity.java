package com.example.youtubeplayer;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button playBtn;
    private EditText editText;

    public static final String DEVELOPER_KEY = "ioweurjidfg979345";

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        playBtn = findViewById(R.id.play_btn);
        editText = findViewById(R.id.edit_url);
        playBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.play_btn) {
                if (checkUrl()) {
                    String inputStr = editText.getText().toString().trim();
                    String videoId = inputStr.substring(17);
                    Intent intent = null;
                    intent = YouTubeStandalonePlayer.createVideoIntent(
                            this, DEVELOPER_KEY, videoId, 0, true, false);
                    if (intent != null) {
                        if (canResolveIntent(intent)) {
                            startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                        } else {
                            // Could not resolve the intent - must need to install or update the YouTube API service.
                            YouTubeInitializationResult.SERVICE_MISSING
                                    .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
                        }
                    }
//                    Intent intent = new Intent(MainActivity.this, YouTubeActivity.class);
//                    intent.putExtra("apiKey", DEVELOPER_KEY);
//                    intent.putExtra("videoId", videoId);
//                    startActivity(intent);
                }
            }

        }

        private boolean checkUrl() {
            String inputStr = editText.getText().toString().trim();
            if (TextUtils.isEmpty(inputStr) || !inputStr.startsWith("https://youtu.be/")) {
                Toast.makeText(this,getString(R.string.input_err),Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private int parseInt(String text, int defaultValue) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return defaultValue;
    }

}