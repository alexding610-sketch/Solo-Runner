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

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Statistics activity showing running history.
 * Supports gesture navigation for different time periods.
 */
public class StatisticsActivity extends AppCompatActivity {
  private static final String TAG = "StatisticsActivity";

  private TextToSpeech tts;
  private boolean ttsInitialized = false;
  private TextView focusIndicator;
  private GestureDetector gestureDetector;

  private List<MenuItem> menuItems;
  private int currentFocusIndex = 0;

  private static class MenuItem {
    String label;
    String period;

    MenuItem(String label, String period) {
      this.label = label;
      this.period = period;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics);

    focusIndicator = findViewById(R.id.focus_indicator);

    // Initialize menu items
    menuItems = new ArrayList<>();
    menuItems.add(new MenuItem("本次运动", "current"));
    menuItems.add(new MenuItem("一周统计", "week"));
    menuItems.add(new MenuItem("一个月统计", "month"));
    menuItems.add(new MenuItem("一年统计", "year"));

    // Initialize TTS
    tts = new TextToSpeech(this, status -> {
      if (status == TextToSpeech.SUCCESS) {
        int result = tts.setLanguage(Locale.CHINESE);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.e(TAG, "Chinese language not supported for TTS");
        } else {
          ttsInitialized = true;
          speakCurrentFocus();
        }
      } else {
        Log.e(TAG, "TTS initialization failed");
      }
    });

    // Initialize gesture detector
    gestureDetector = new GestureDetector(this, new GestureListener());

    updateFocusIndicator();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      selectCurrentItem();
      return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      float diffX = e2.getX() - e1.getX();
      float diffY = e2.getY() - e1.getY();

      if (Math.abs(diffX) > Math.abs(diffY)) {
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
          if (diffX > 0) {
            onSwipeRight();
          } else {
            onSwipeLeft();
          }
          return true;
        }
      }
      return false;
    }
  }

  private void onSwipeLeft() {
    if (currentFocusIndex > 0) {
      currentFocusIndex--;
      updateFocusIndicator();
      speakCurrentFocus();
    }
  }

  private void onSwipeRight() {
    if (currentFocusIndex < menuItems.size() - 1) {
      currentFocusIndex++;
      updateFocusIndicator();
      speakCurrentFocus();
    }
  }

  private void selectCurrentItem() {
    MenuItem item = menuItems.get(currentFocusIndex);
    if (ttsInitialized) {
      tts.speak("查看" + item.label, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // Start the detail activity
    Intent intent = new Intent(this, StatisticsDetailActivity.class);
    intent.putExtra("period", item.period);
    intent.putExtra("title", item.label);
    startActivity(intent);
  }

  private void updateFocusIndicator() {
    MenuItem item = menuItems.get(currentFocusIndex);
    focusIndicator.setText(item.label);
  }

  private void speakCurrentFocus() {
    if (ttsInitialized) {
      MenuItem item = menuItems.get(currentFocusIndex);
      tts.speak(item.label, TextToSpeech.QUEUE_FLUSH, null, null);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (tts != null) {
      tts.stop();
      tts.shutdown();
    }
  }
}
