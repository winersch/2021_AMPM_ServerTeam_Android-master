package kr.ac.jbnu.sw.ServerTeamTestApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kr.ac.jbnu.sw.ServerTeamTestApplication.controller.ServerDataAdapter;
import kr.ac.jbnu.sw.ServerTeamTestApplication.model.GlobalStorage;
import kr.ac.jbnu.sw.ServerTeamTestApplication.view.DataAddActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public GlobalStorage globalStorage;

    private ServerDataAdapter serverDataAdapter;

    private ListView serverDataListView;

    private Button serverDataReceiveButton;
    private Button addDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalStorage = GlobalStorage.getInstance();

        serverDataReceiveButton = (Button)findViewById(R.id.main_data_receive_button);
        addDataButton = (Button)findViewById(R.id.main_data_add_button);

        serverDataAdapter = new ServerDataAdapter(this);
        serverDataListView = (ListView)findViewById(R.id.main_listview);
        serverDataListView.setAdapter(serverDataAdapter);

        serverDataReceiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveServerData();
            }
        });

        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addDataIntent = new Intent(getApplicationContext(), DataAddActivity.class);
                startActivityForResult(addDataIntent, 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ServerTeamTest 종료");
        builder.setMessage("정말로 ServerTeamTest를 종료하시겠습니까?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == DataAddActivity.ADD_SUCCESS) {
                receiveServerData();
            }
        }

    }

    private void receiveServerData() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient okHttpClient = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://ampmservertest.namsu.site:8080/sechang-0.0.1-SNAPSHOT/").newBuilder();
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    makeMainActivityToast(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String data = response.body().string();

                        if (response.code() == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        globalStorage.setReceiveServerDataMap(new ObjectMapper().readValue(data, HashMap.class));
                                        serverDataAdapter.notifyDataSetChanged();

                                        makeMainActivityToast("데이터를 불러왔습니다.");

                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else if (response.code() == 404) {
                            makeMainActivityToast(data);

                        } else if (response.code() == 400) {
                            makeMainActivityToast(data);
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void makeMainActivityToast(String data) {
        if (data != null && !data.equals("")) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                }
            }, 0);
        }
    }
}