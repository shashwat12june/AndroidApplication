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

public class HomeScreen extends AppCompatActivity implements View.OnClickListener,TextToSpeech.OnInitListener{
ImageView pnr,liveStatus,resschedule,canceledtrain;
Button signout,btnspeak;
    private TextToSpeech tts;
Intent i;
String url,result;
    ProgressDialog dialog;
    AlertDialog.Builder builder;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
pnr=findViewById(R.id.pnrStataus);
liveStatus=findViewById(R.id.LiveTrainStataus);
resschedule=findViewById(R.id.reschedule);
        builder = new AlertDialog.Builder(this);
btnspeak=findViewById(R.id.btnSpeak);
canceledtrain=findViewById(R.id.canceledtrain);
        signout=findViewById(R.id.signout);
        tts = new TextToSpeech(this, this);
pnr.setOnClickListener(this);
resschedule.setOnClickListener(this);
liveStatus.setOnClickListener(this);
signout.setOnClickListener(this);
btnspeak.setOnClickListener(this);
canceledtrain.setOnClickListener(this);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading..");
i=new Intent(this,MainActivity.class);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.LiveTrainStataus:
            {
                Bundle b=new Bundle();
                b.putString("message","Please enter train number and date in given format");
                b.putString("hint","<train number>/date/<dd-mm-yyyy>");
                b.putString("type","LiveStatus");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            case R.id.pnrStataus:
            {
                Bundle b=new Bundle();
                b.putString("message","Please enter your pnr number");
                b.putString("hint","pnr/<your pnr>");
                b.putString("type","pnr");
                i.putExtras(b);
                startActivity(i);
                break;
            }
            case R.id.signout:
            {
                Login_Activity.mAuth.signOut();
                Login_Activity.mGoogleSignInClient.signOut();
                Intent i=new Intent(this,Login_Activity.class);
                startActivity(i);
                finish();
                break;}

            case R.id.canceledtrain:
            {
                Bundle b=new Bundle();
                b.putString("message","Please enter date");
                b.putString("hint","c/<dd-mm-yyyy>");
                b.putString("type","canceledtrains");
                i.putExtras(b);
                startActivity(i);
                break;}
            case R.id.reschedule:
            {
                Bundle b=new Bundle();
                b.putString("message","Please enter date");
                b.putString("hint","r/<dd-mm-yyyy>");
                b.putString("type","rescheduledtrains");
                i.putExtras(b);
                startActivity(i);
                break;}
            case R.id.btnSpeak:
            {

             promptSpeechInput();
            }

        }
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
                    Toast.makeText(getApplicationContext(),"Running... ",Toast.LENGTH_LONG).show();
                    makeStringRequest(result.get(0).toString());


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
                        result=response.toString();
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
            //v

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //  Log.e("TTS", "This Language is not supported");
            } else {
                speakOut();
            }

        }
       else {
            // Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut() {
        tts.setSpeechRate(1/2);

        tts.speak(result, TextToSpeech.QUEUE_FLUSH, null);
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
