package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projectUtil.BeCutErrorVideoSpan;
import com.projectUtil.BeCutSubtitleSpan;
import com.projectUtil.TextAdapter;
import com.projectUtil.VideoAdapter;
import com.zhaoss.weixinrecorded.R;

import java.util.ArrayList;
import java.util.List;

public class TextActivity extends AppCompatActivity {
    private List<BeCutSubtitleSpan> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        ListView listView=(ListView) findViewById(R.id.list_Main);

        initText();//初始化字幕数据,需要到内存中去寻找
        TextAdapter Myadapter=new TextAdapter(TextActivity.this,R.layout.video_item,mylist);

        listView.setAdapter(Myadapter);
        // 设置ListView的单击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * @param parent
             *            ListView
             * @param view
             *            所点击的item视图，也就是TextView
             * @param position
             *            所点击item的位置
             * @param id
             *            所点击item的id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String content = textView.getText().toString();

                    Toast.makeText(TextActivity.this, "点击了 " + content,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TextActivity.this,EditVideoActivity.class);
                    BeCutSubtitleSpan myBeSubtitleSpan=mylist.get(position);
                    Long startTime=myBeSubtitleSpan.startTime;
                    intent.putExtra("startTime",startTime);
                    startActivity(intent);
                }
            }
        });

        // 设置ListView的长按事件，后期可以做成单击跳转，长按修改文本内容这样的
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            /**
             * @param parent
             *            ListView
             * @param view
             *            所点击的item视图，也就是TextView
             * @param position
             *            所点击item的位置
             * @param id
             *            所点击item的id
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String content = textView.getText().toString();

                    Toast.makeText(TextActivity.this, "长按了 " + content,
                            Toast.LENGTH_SHORT).show();
                }
                // 返回true，表示将单击事件进行拦截
                return true;
            }
        });

    }

    //TODO:将内存的数据读入到mylist中
    private void initText(){
        //EditVideoActivity editVideoActivity=new EditVideoActivity();
        //editVideoActivity.refreshAllClips();
        mylist=EditVideoActivity.allClips;
    }
}