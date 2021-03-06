package com.ssappclinic.railwayassistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Network.Volley_Request;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
Button signOut;
String results;
    EditText txtSpeechInput;
    private TextToSpeech tts,tts2;
    String search,message,type,url,hint;
  ImageView img;
    EditText result;
    Button btnSpeak,go;
ProgressDialog dialog;
    AlertDialog.Builder builder;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
Bundle b=getIntent().getExtras();
      message=b.getString("message");
      type=b.getString("type");
      hint=b.getString("hint");
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        tts = new TextToSpeech(this, this);
        tts2 = new TextToSpeech(this, this);

        builder = new AlertDialog.Builder(this);
        signOut=findViewById(R.id.signout);
        img=findViewById(R.id.imgView);
        txtSpeechInput =  findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
       go = (Button) findViewById(R.id.go);
        result=findViewById(R.id.response);
     dialog=new ProgressDialog(this);
     dialog.setMessage("Loading");
     if(type.equals("pnr"))
     {
         img.setImageResource(R.drawable.pnr);
     }
     else  if(type.equals("LiveStatus"))
     {
         img.setImageResource(R.drawable.trainlive);
     }
     else  if(type.equals("canceledtrains"))
     {
         img.setImageResource(R.drawable.canceledtrain);
     }
     else  if(type.equals("rescheduledtrains"))
     {
         img.setImageResource(R.drawable.reschdl);
     }
        speakMessage();
        txtSpeechInput.setHint(hint);
go.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        makeStringRequest(txtSpeechInput.getText().toString());
    }
});

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Login_Activity.mAuth.signOut();
                                           Login_Activity.mGoogleSignInClient.signOut();
                                           Intent i=new Intent(MainActivity.this,Login_Activity.class);
                                           startActivity(i);
                                           finish();

                                       }
                                   }
        );
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    Toast.makeText(getApplicationContext(),"Running... ",Toast.LENGTH_LONG).show();
                    makeStringRequest(txtSpeechInput.getText().toString());


                }
                break;
            }

        }
    }

    public void makeStringRequest(final String text)
    {

         url = "http://shanky.xyz:5000/?q="+text;

        dialog.show();
        StringRequest StringReq = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
       // result.setText(response.toString());
                        builder.setTitle("Result");
                        builder.setMessage(response.toString());
                        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                if (tts != null) {
                                    tts.stop();
                                    //tts.shutdown();
                                }
                            }

                        });
                        result.setText(response.toString());
                        speakOut();
                        builder.show();
       /* Intent i=new Intent(getApplication(),Main2Activity.class);
        Bundle b=new Bundle();
        b.putString("response",response.toString());
        i.putExtras(b);
        startActivity(i);*/
                        dialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // hide the progress dialog
                result.setText(error.toString());
                dialog.hide();
            }
        }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String>  params = new HashMap<String, String>();
            params.put("Ocp-Apim-Subscription-Key", "96d05359d76f4e758906539daeab939e");

            return params;
        }
    };
        StringReq.setRetryPolicy(new DefaultRetryPolicy(100000,5,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley_Request.getVolleyInstance(this).addRequestToQueue(StringReq);
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

        }
        if (status == TextToSpeech.SUCCESS) {

            int result = tts2.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //  Log.e("TTS", "This Language is not supported");
            } else {
                speakMessage();
            }

        }else {
            // Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut() {
        tts.setSpeechRate(1/2);
       String text = result.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    private void speakMessage() {
        tts2.setSpeechRate(1/2);
        tts2.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (tts2 != null) {
            tts2.stop();
            tts2.shutdown();
        }
        super.onDestroy();
    }
}
