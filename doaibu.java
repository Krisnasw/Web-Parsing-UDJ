import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
public class QuizActivity extends ActionBarActivity {
    private TextView quizQuestion;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        quizQuestion = (TextView) findViewById(R.id.quiz_question);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionOne = (RadioButton) findViewById(R.id.radio0);
        optionTwo = (RadioButton) findViewById(R.id.radio1);
        optionThree = (RadioButton) findViewById(R.id.radio2);
        optionFour = (RadioButton) findViewById(R.id.radio3);
        final Button previousButton = (Button) findViewById(R.id.previousquiz);
        Button nextButton = (Button) findViewById(R.id.nextquiz);
        result = (TextView) findViewById(R.id.resultView);

        AsyncJsonObject asyncObject = new AsyncJsonObject();
        asyncObject.execute("");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(" ");
                if (currentQuizQuestion >= quizCount) {
                     final Intent intent = new Intent(QuizActivity.this, complete.class);
                     startActivity(intent);
                    return;
                } else {

                    previousButton.setVisibility(Button.VISIBLE);
                    optionOne.setTextColor(Color.BLACK);
                    optionTwo.setTextColor(Color.BLACK);
                    optionThree.setTextColor(Color.BLACK);
                    optionFour.setTextColor(Color.BLACK);



                    firstQuestion = parsedObject.get(currentQuizQuestion);
                    quizQuestion.setText(firstQuestion.getQuestion());
                    String[] possibleAnswers = firstQuestion.getAnswers().split(",");
                    uncheckedRadioButton();
                    optionOne.setText(possibleAnswers[0]);
                    optionTwo.setText(possibleAnswers[1]);
                    optionThree.setText(possibleAnswers[2]);
                    optionFour.setText(possibleAnswers[3]);


                
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
                    if (correctAnswerForQuestion==1)
                    {
                        optionOne.setTextColor(getResources().getColor(R.color.green));

                    }
                    else if (correctAnswerForQuestion==2)
                    {
                        optionTwo.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if (correctAnswerForQuestion==3)
                    {
                        optionThree.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if (correctAnswerForQuestion==4)
                    {
                        optionFour.setTextColor(getResources().getColor(R.color.green));
                    }
                    score=score+1;
                   
                    currentQuizQuestion++;

                } else {
// failed question
                    result.setTextColor(Color.parseColor("#FF0000"));
                    if (correctAnswerForQuestion==1)
                    {
                        optionOne.setTextColor(getResources().getColor(R.color.green));

                    }
                    else if (correctAnswerForQuestion==2)
                    {
                        optionTwo.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if (correctAnswerForQuestion==3)
                    {
                        optionThree.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if (correctAnswerForQuestion==4)
                    {
                        optionFour.setTextColor(getResources().getColor(R.color.green));
                    }


                    currentQuizQuestion++;
                    return;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncJsonObject extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost("http://yoursite.com/quiz.php");
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
            String[] possibleAnswers = firstQuestion.getAnswers().split(",");
            optionOne.setText(possibleAnswers[0]);
            optionTwo.setText(possibleAnswers[1]);
            optionThree.setText(possibleAnswers[2]);
            optionFour.setText(possibleAnswers[3]);



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
                String answerOptions = jsonChildNode.getString("possible_answers");
                int correctAnswer = jsonChildNode.getInt("correct_answer");
                newItemObject = new QuizWrapper(id, question, answerOptions, correctAnswer);
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