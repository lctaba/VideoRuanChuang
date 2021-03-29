package com.zhaoss.weixinrecordeddemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhaoss.weixinrecorded.activity.RecordedActivity;
import com.zhaoss.weixinrecorded.activity.SelectActivity;

public class MainActivity extends AppCompatActivity {



    private TextView tv_path;
    private TextureView textureView;
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Button button_add = findViewById(R.id.button3);
        Button button_select = findViewById(R.id.button4);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示输入文件名的提示框
                final EditText inputServer = new EditText(MainActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("输入文件名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       String text = inputServer.getText().toString();
                        //界面跳转
                        Intent intent = new Intent(MainActivity.this, RecordedActivity.class);
                        intent.putExtra("fileName", text);
                        startActivity(intent);
                    }

                    ;
                });
                builder.show();
            }
        });

        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
            }
        });

    }
}