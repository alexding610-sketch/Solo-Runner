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

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

/**
 * Statistics detail activity showing detailed running data for a specific period.
 */
public class StatisticsDetailActivity extends AppCompatActivity {
  private static final String TAG = "StatisticsDetailActivity";

  private TextToSpeech tts;
  private boolean ttsInitialized = false;
  private TextView titleText;
  private TextView dataText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_statistics_detail);

    titleText = findViewById(R.id.title_text);
    dataText = findViewById(R.id.data_text);

    String period = getIntent().getStringExtra("period");
    String title = getIntent().getStringExtra("title");

    titleText.setText(title);

    // Initialize TTS
    tts = new TextToSpeech(this, status -> {
      if (status == TextToSpeech.SUCCESS) {
        int result = tts.setLanguage(Locale.CHINESE);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.e(TAG, "Chinese language not supported for TTS");
        } else {
          ttsInitialized = true;
          speakStatistics();
        }
      } else {
        Log.e(TAG, "TTS initialization failed");
      }
    });

    loadStatistics(period);
  }

  private void loadStatistics(String period) {
    // TODO: Load actual statistics from database
    String data = "暂无数据";

    switch (period) {
      case "current":
        data = "本次运动数据\n\n运动时长: 00:00:00\n距离: 0.0 公里\n平均配速: 0:00 /公里";
        break;
      case "week":
        data = "本周运动数据\n\n总运动次数: 0 次\n总时长: 00:00:00\n总距离: 0.0 公里";
        break;
      case "month":
        data = "本月运动数据\n\n总运动次数: 0 次\n总时长: 00:00:00\n总距离: 0.0 公里";
        break;
      case "year":
        data = "本年运动数据\n\n总运动次数: 0 次\n总时长: 00:00:00\n总距离: 0.0 公里";
        break;
    }

    dataText.setText(data);
  }

  private void speakStatistics() {
    if (ttsInitialized) {
      String title = getIntent().getStringExtra("title");
      tts.speak(title + "，" + dataText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
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
