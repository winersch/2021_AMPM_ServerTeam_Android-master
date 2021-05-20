package kr.ac.jbnu.sw.ServerTeamTestApplication.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kr.ac.jbnu.sw.ServerTeamTestApplication.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataAddActivity extends AppCompatActivity {
    public static final String TAG = "DataAddActivity";

    public static final int ADD_SUCCESS = 1;
    public static final int ADD_FAIL = 2;
    public static final int USER_CANCEL = 3;

    private Button sendDataToServerButton;
    private Button userCancelButton;

    private EditText userIdEditText;
    private EditText userNameEditText;
    private EditText userAgeEditText;
    private EditText userDescEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        sendDataToServerButton = (Button)findViewById(R.id.add_send_server_button);
        userCancelButton = (Button)findViewById(R.id.add_exit);

        userIdEditText = (EditText)findViewById(R.id.input_text_value_1);
        userNameEditText = (EditText)findViewById(R.id.input_text_value_2);
        userAgeEditText = (EditText)findViewById(R.id.input_text_value_3);
        userDescEditText = (EditText)findViewById(R.id.input_text_value_4);

        sendDataToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataAddActivity.this);
                builder.setTitle("확인");
                builder.setMessage("정말로 위와 같은 데이터를 전송하시겠습니까?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!userIdEditText.getText().toString().equals("") && !userAgeEditText.getText().toString().equals("")
                                && !userNameEditText.getText().toString().equals("") && !userDescEditText.getText().toString().equals("")){


                            HashMap<String, String> userInputHashMap = new HashMap<String, String>() {{
                                put("name", userNameEditText.getText().toString());
                                put("age", userAgeEditText.getText().toString());
                                put("desc", userDescEditText.getText().toString());
                            }};

                            postServerData(userIdEditText.getText().toString(), userInputHashMap);
                        }
                        setResult(ADD_SUCCESS);
                        finish();
                    }

                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

        userCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(USER_CANCEL);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("확인");
        builder.setMessage("정말로 데이터 추가를 종료 하시겠습니까?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setResult(USER_CANCEL);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void postServerData(String id, HashMap<String, String> userHashMap) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient okHttpClient = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://ampmservertest.namsu.site:8080/sechang-0.0.1-SNAPSHOT/post/" + id).newBuilder();
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), new JSONObject(userHashMap).toString()))
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String data = response.body().string();

                        Log.d(TAG, "----- Send Data : " + data + " -----");

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
