package intivestudio.web.id.udjseamolec;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = Profile.class.getSimpleName();
    private TextView quizQuestion;
    private ImageView quizImage;
    private TextView result;
    private RadioGroup radioGroup;
    private RadioButton optionOne;
    private RadioButton optionTwo;
    private RadioButton optionThree;
    private RadioButton optionFour;
    private int currentQuizQuestion;
    private int quizCount;
    private QuizWrapper firstQuestion;
    private List<QuizWrapper> parsedObject;
    public int correctAnswerForQuestion;
    public int score;

    private int ScoreString;
    String Score = ScoreString + "";

    private SessionManager session;
    private SQLiteFunction func;
    private SQLiteHandler db;

    public ImageLoader imageLoader;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        quizQuestion = (TextView) findViewById(R.id.quiz_question);
        quizImage = (ImageView) findViewById(R.id.imageSoal);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionOne = (RadioButton) findViewById(R.id.radio0);
        optionTwo = (RadioButton) findViewById(R.id.radio1);
        optionThree = (RadioButton) findViewById(R.id.radio2);
        optionFour = (RadioButton) findViewById(R.id.radio3);
        final ImageView previousButton = (ImageView) findViewById(R.id.previousquiz);
        ImageView nextButton = (ImageView) findViewById(R.id.nextquiz);
        result = (TextView) findViewById(R.id.resultView);

        func = new SQLiteFunction(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ujian Dalam Jaringan");

        AsyncJsonObject asyncObject = new AsyncJsonObject();
        asyncObject.execute("");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(" ");
                if (currentQuizQuestion >= quizCount) {
                    AlertDialog tampilKotakAlert;
                    tampilKotakAlert = new AlertDialog.Builder(QuizActivity.this)
                            .create();
                    tampilKotakAlert.setTitle("Hasil Ujian");
                    tampilKotakAlert.setIcon(R.mipmap.ic_launcher);
                    tampilKotakAlert.setMessage("Nilai : " + correctAnswerForQuestion * 10);
                    ScoreString = correctAnswerForQuestion * 10;

                    tampilKotakAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Keluar",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    Intent q = new Intent(QuizActivity.this, Profile.class);
                                    finish();
                                    startActivity(q);
                                }
                            });

                    tampilKotakAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Submit",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HashMap<String, String> user = db.getUserDetails();

                                    String name = user.get("name");
                                    String ids = user.get("uid");
                                    String Score = ScoreString +"";

                                    InputNilai(name, Score, ids);
                                }
                            });

                    tampilKotakAlert.show();
                    return;
                } else {

                    previousButton.setVisibility(Button.VISIBLE);
                    optionOne.setTextColor(Color.BLACK);
                    optionTwo.setTextColor(Color.BLACK);
                    optionThree.setTextColor(Color.BLACK);
                    optionFour.setTextColor(Color.BLACK);


                    firstQuestion = parsedObject.get(currentQuizQuestion);
                    quizQuestion.setText(firstQuestion.getQuestion());
                    imageLoader.DisplayImage(firstQuestion.getImage(), quizImage);
//                    String[] possibleAnswers = firstQuestion.getAnswers().split(",");
                    String pila = firstQuestion.getPila();
                    String pilb = firstQuestion.getPilb();
                    String pilc = firstQuestion.getPilc();
                    String pild = firstQuestion.getPild();
                    System.out.println("Jawaban : "+pila);
                    uncheckedRadioButton();
                    optionOne.setText(pila);
                    optionTwo.setText(pilb);
                    optionThree.setText(pilc);
                    optionFour.setText(pild);

                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                previousButton.setVisibility(Button.INVISIBLE);
                int radioSelected = radioGroup.getCheckedRadioButtonId();
                int userSelection = getSelectedAnswer(radioSelected);
                correctAnswerForQuestion = firstQuestion.getCorrectAnswer();


                if (userSelection == correctAnswerForQuestion) {
// correct answer
                    if (correctAnswerForQuestion == 1) {
                        optionOne.setTextColor(getResources().getColor(R.color.colorAccent));

                    } else if (correctAnswerForQuestion == 2) {
                        optionTwo.setTextColor(getResources().getColor(R.color.colorAccent));
                    } else if (correctAnswerForQuestion == 3) {
                        optionThree.setTextColor(getResources().getColor(R.color.colorAccent));
                    } else if (correctAnswerForQuestion == 4) {
                        optionFour.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    score = score + 1;

                    currentQuizQuestion++;

                } else {
// failed question
                    result.setTextColor(Color.parseColor("#FF0000"));
                    if (correctAnswerForQuestion == 1) {
                        optionOne.setTextColor(getResources().getColor(R.color.colorPrimary));

                    } else if (correctAnswerForQuestion == 2) {
                        optionTwo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (correctAnswerForQuestion == 3) {
                        optionThree.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (correctAnswerForQuestion == 4) {
                        optionFour.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }


                    currentQuizQuestion++;
                    return;
                }

            }
        });
    }

    private class AsyncJsonObject extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost("http://192.168.137.51/droid/quiz.php");
            String jsonResult = "";
            try {
                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
                System.out.println("Returned Json object " + jsonResult.toString());
            } catch (ClientProtocolException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return jsonResult;
        }

        @Override
        protected void onPreExecute() {
// TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = ProgressDialog.show(QuizActivity.this, "Loading Quiz", "Please Wait....", true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            System.out.println("Resulted Value: " + result);
            parsedObject = returnParsedJsonObject(result);
            if (parsedObject == null) {
                return;
            }
            quizCount = parsedObject.size();
            firstQuestion = parsedObject.get(0);
            quizQuestion.setText(firstQuestion.getQuestion());
            imageLoader.DisplayImage(firstQuestion.getImage(), quizImage);
//            String[] possibleAnswers = firstQuestion.getAnswers().split(",");
            String pila = firstQuestion.getPila();
            String pilb = firstQuestion.getPilb();
            String pilc = firstQuestion.getPilc();
            String pild = firstQuestion.getPild();
            uncheckedRadioButton();
            optionOne.setText(pila);
            optionTwo.setText(pilb);
            optionThree.setText(pilc);
            optionFour.setText(pild);


        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }

    private List<QuizWrapper> returnParsedJsonObject(String result) {
        List<QuizWrapper> jsonObject = new ArrayList<QuizWrapper>();
        JSONObject resultObject = null;
        JSONArray jsonArray = null;
        QuizWrapper newItemObject = null;
        try {
            resultObject = new JSONObject(result);
            System.out.println("Testing the water " + resultObject.toString());
            jsonArray = resultObject.optJSONArray("quiz_questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonChildNode = null;
            try {
                jsonChildNode = jsonArray.getJSONObject(i);
                int id = jsonChildNode.getInt("id");
                String question = jsonChildNode.getString("question");
                String image = jsonChildNode.getString("image");
                String pila = jsonChildNode.getString("pila");
                String pilb = jsonChildNode.getString("pilb");
                String pilc = jsonChildNode.getString("pilc");
                String pild = jsonChildNode.getString("pild");
                int correctAnswer = jsonChildNode.getInt("correct_answer");
                newItemObject = new QuizWrapper(id, question,image, pila, pilb, pilc, pild, correctAnswer);
                jsonObject.add(newItemObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private int getSelectedAnswer(int radioSelected) {
        int answerSelected = 0;
        if (radioSelected == R.id.radio0) {
            answerSelected = 1;
        }
        if (radioSelected == R.id.radio1) {
            answerSelected = 2;
        }
        if (radioSelected == R.id.radio2) {
            answerSelected = 3;
        }
        if (radioSelected == R.id.radio3) {
            answerSelected = 4;
        }
        return answerSelected;
    }

    private void uncheckedRadioButton() {
        optionOne.setChecked(false);
        optionTwo.setChecked(false);
        optionThree.setChecked(false);
        optionFour.setChecked(false);
        radioGroup.clearCheck();
    }

    private void InputNilai(final String name,final String Score, final String ids) {

        String tag_string_req = "req_input";

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_HASIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Input Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Now store the user in SQLite
                        String uid = jObj.getString("id_users");

                        JSONObject user = jObj.getJSONObject("hasil");
                        String ids = user.getString("ids");
                        String name = user.getString("nama");
                        String nilai = user.getString("nilai");

                        // Inserting row in users table
                        func.addNilai(ids, name, nilai);

                        // Launch main activity
                        Intent intent = new Intent(QuizActivity.this,
                                Profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Input Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_users", ids);
                params.put("nama", name);
                params.put("nilai", Score);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}