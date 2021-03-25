package com.zhaoss.weixinrecorded.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhaoss.weixinrecorded.R;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_add=findViewById(R.id.button);
        Button button_selext=findViewById(R.id.button2);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示输入文件名的提示框
                final EditText inputServer = new EditText(FirstActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);

                builder.setTitle("输入文件名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                final String[] text = new String[1];
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        text[0] = inputServer.getText().toString();

                    };
                });
                builder.show();

                //界面跳转
                Intent intent=new Intent(FirstActivity.this,RecordedActivity.class);
                intent.putExtra("fileName", text[0]);
                startActivity(intent);
            }
        });

    }
}