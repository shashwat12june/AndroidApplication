package com.ssappclinic.railwayassistant;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements TextToSpeech.OnInitListener{
protected TextView response;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tts = new TextToSpeech(this, this);
        response=findViewById(R.id.response);
        Bundle b=getIntent().getExtras();
        response.setText(b.getString("response"));
        speakOut();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
              //  Log.e("TTS", "This Language is not supported");
            } else {
                speakOut();
            }

        } else {
           // Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut() {
        tts.setSpeechRate(1/2);
        String text = response.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
