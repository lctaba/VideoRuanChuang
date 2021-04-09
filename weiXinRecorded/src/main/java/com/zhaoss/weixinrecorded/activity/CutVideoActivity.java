package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projectUtil.BeCutErrorVideoSpan;
import com.projectUtil.BeCutSubtitleSpan;
import com.zhaoss.weixinrecorded.adpter.CutVideoAdapter;
import com.zhaoss.weixinrecorded.R;

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
                if (view instanceof View) {
                    TextView textView=view.findViewById(R.id.video_name);
                    String content = textView.getText().toString();
                    Toast.makeText(CutVideoActivity.this, "跳转到 " + content,
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CutVideoActivity.this,EditVideoActivity.class);
                    BeCutErrorVideoSpan myBeSubtitleSpan=myList.get(position);
                    Long startTime=myBeSubtitleSpan.startTime;
                    intent.putExtra("startTime",startTime);
                    startActivity(intent);
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
                if (view instanceof View) {
                    TextView textView=view.findViewById(R.id.video_name);
                    String content = textView.getText().toString();

                    Toast.makeText(CutVideoActivity.this, "长按了 " + content,
                            Toast.LENGTH_SHORT).show();

                    BeCutErrorVideoSpan beCutErrorVideoSpan = myList.get(position);

                    if( beCutErrorVideoSpan.isChecked==false){
                        Toast.makeText(CutVideoActivity.this, "选中了 " + content,
                                Toast.LENGTH_SHORT).show();
                        EditVideoActivity.addErrorVideo(beCutErrorVideoSpan);
                        textView.setTextColor(Color.rgb(255, 0, 0));
                        beCutErrorVideoSpan.isChecked=true;
                        //isDelete[position] = true;
                    }else {
                        EditVideoActivity.removeErrorVideo(beCutErrorVideoSpan);
                        Toast.makeText(CutVideoActivity.this, "取消选中选中了 " + content,
                                Toast.LENGTH_SHORT).show();
                        textView.setTextColor(Color.rgb(0, 0, 0));
                        beCutErrorVideoSpan.isChecked=false;
                        //isDelete[position] = false;
                    }
                }
                // 返回true，表示将单击事件进行拦截
                return true;
            }
        });
    }

    //TODO:在内存中读取数据并导入mylist
    private void initProjects(){
        myList=EditVideoActivity.allErrorVideo;
    }
}