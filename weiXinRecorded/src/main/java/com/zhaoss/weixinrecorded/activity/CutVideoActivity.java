package com.zhaoss.weixinrecorded.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projectUtil.BeCutErrorVideoSpan;
import com.projectUtil.CutVideoAdapter;
import com.projectUtil.Project;
import com.projectUtil.VideoAdapter;
import com.zhaoss.weixinrecorded.R;

import java.util.ArrayList;
import java.util.List;

public class CutVideoActivity extends AppCompatActivity {

    private List<BeCutErrorVideoSpan> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        initProjects();//初始化视频数据,需要到内存中去寻找
        CutVideoAdapter Myadapter=new CutVideoAdapter(CutVideoActivity.this,R.layout.video_item,myList);

        ListView listView=(ListView) findViewById(R.id.list_Main);


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

                    Toast.makeText(CutVideoActivity.this, "点击了 " + content,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置ListView的长按事件
        //TODO:修改！！！
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

                    Toast.makeText(CutVideoActivity.this, "长按了 " + content,
                            Toast.LENGTH_SHORT).show();
                }
                // 返回true，表示将单击事件进行拦截
                return true;
            }
        });
    }

    //TODO:在内存中读取数据并导入mylist
    private void initProjects(){
        EditVideoActivity editVideoActivity=new EditVideoActivity();
        editVideoActivity.refreshAllErrorVideo();
        myList=editVideoActivity. allErrorVideo;
    }
}