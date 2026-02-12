// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.research.guideline.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.research.guideline.classic.GuidelineClassicFragment;
import com.google.research.guideline.inject.Qualifiers.GuidanceFragment;
import com.google.research.guideline.util.permissions.RequiredPermissionsHelper;
import com.google.research.guideline.util.ui.ImmersiveModeController;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.Locale;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Running activity with timer and gesture controls.
 * Double tap to pause, long press to finish.
 */
@AndroidEntryPoint(AppCompatActivity.class)
public class RunningActivity extends Hilt_RunningActivity {
  private static final String TAG = "RunningActivity";
  private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

  @Inject ImmersiveModeController immersiveModeController;
  @Inject RequiredPermissionsHelper permissionsHelper;
  @Inject @GuidanceFragment Provider<Fragment> guidanceFragmentProvider;

  private TextView statusText;
  private TextView timerText;
  private TextView instructionText1;
  private TextView instructionText2;

  private TextToSpeech tts;
  private boolean ttsInitialized = false;
  private GestureDetector gestureDetector;

  private long startTime = 0;
  private long pauseTime = 0;
  private boolean isRunning = false;
  private boolean isPaused = false;
  private Handler timerHandler = new Handler();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_running);

    statusText = findViewById(R.id.status_text);
    timerText = findViewById(R.id.timer_text);
    instructionText1 = findViewById(R.id.instruction_text1);
    instructionText2 = findViewById(R.id.instruction_text2);

    immersiveModeController.enableImmersiveMode();

    // Initialize TTS
    tts = new TextToSpeech(this, status -> {
      if (status == TextToSpeech.SUCCESS) {
        int result = tts.setLanguage(Locale.CHINESE);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.e(TAG, "Chinese language not supported for TTS");
        } else {
          ttsInitialized = true;
        }
      } else {
        Log.e(TAG, "TTS initialization failed");
      }
    });

    // Initialize gesture detector
    gestureDetector = new GestureDetector(this, new GestureListener());

    permissionsHelper.checkRequiredPermissions(this, REQUIRED_PERMISSIONS, this::startRunning);
  }

  private void startRunning() {
    // Load the guidance fragment
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.camera_container, guidanceFragmentProvider.get())
        .commitNow();

    // Start timer
    startTime = SystemClock.elapsedRealtime();
    isRunning = true;
    isPaused = false;
    updateTimer();

    if (ttsInitialized) {
      tts.speak("开始运动", TextToSpeech.QUEUE_FLUSH, null, null);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      togglePause();
      return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      finishRunning();
    }
  }

  private void togglePause() {
    if (!isRunning) return;

    if (isPaused) {
      // Resume
      startTime += SystemClock.elapsedRealtime() - pauseTime;
      isPaused = false;
      statusText.setText("正在跑步");
      updateTimer();
      if (ttsInitialized) {
        tts.speak("继续运动", TextToSpeech.QUEUE_FLUSH, null, null);
      }
    } else {
      // Pause
      pauseTime = SystemClock.elapsedRealtime();
      isPaused = true;
      statusText.setText("已暂停");
      timerHandler.removeCallbacks(timerRunnable);
      if (ttsInitialized) {
        tts.speak("暂停", TextToSpeech.QUEUE_FLUSH, null, null);
      }
    }
  }

  private void finishRunning() {
    if (!isRunning) return;

    isRunning = false;
    timerHandler.removeCallbacks(timerRunnable);

    long elapsedTime = isPaused ? (pauseTime - startTime) : (SystemClock.elapsedRealtime() - startTime);

    if (ttsInitialized) {
      tts.speak("运动结束", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // TODO: Save running data to database

    finish();
  }

  private void updateTimer() {
    if (!isRunning || isPaused) return;

    timerHandler.postDelayed(timerRunnable, 0);
  }

  private Runnable timerRunnable = new Runnable() {
    @Override
    public void run() {
      if (!isRunning || isPaused) return;

      long elapsedTime = SystemClock.elapsedRealtime() - startTime;
      int seconds = (int) (elapsedTime / 1000);
      int minutes = seconds / 60;
      int hours = minutes / 60;
      seconds = seconds % 60;
      minutes = minutes % 60;

      timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
      timerHandler.postDelayed(this, 1000);
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();
    timerHandler.removeCallbacks(timerRunnable);
    if (tts != null) {
      tts.stop();
      tts.shutdown();
    }
  }
}
