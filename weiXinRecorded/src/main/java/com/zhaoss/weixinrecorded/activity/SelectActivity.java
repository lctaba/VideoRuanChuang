package com.zhaoss.weixinrecorded.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projectUtil.Project;
import com.projectUtil.ProjectUtil;
import com.zhaoss.weixinrecorded.adpter.VideoAdapter;
import com.zhaoss.weixinrecorded.R;

import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private List<Project>projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        initProjects();//初始化视频数据,需要到内存中去寻找
        VideoAdapter Myadapter=new VideoAdapter(SelectActivity.this,R.layout.video_item,projectList);

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

                    Toast.makeText(SelectActivity.this, "点击了 " + content,
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

                    Toast.makeText(SelectActivity.this, "长按了 " + content,
                            Toast.LENGTH_SHORT).show();
                }
                // 返回true，表示将单击事件进行拦截
                return true;
            }
        });
    }

    //TODO:在内存中读取数据并导入
    private void initProjects(){
        projectList= ProjectUtil.getProjectList(this.getExternalFilesDir(null).getPath());
    }
}