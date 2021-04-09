package com.zhaoss.weixinrecorded.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectUtil.Project;
import com.zhaoss.weixinrecorded.R;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<Project> {
    private int resourceId;
    public VideoAdapter(Context context,
                        int textViewResourceId,
                        List<Project>object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View coverView, ViewGroup parent){
        Project project=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView VideoView=view.findViewById(R.id.video_image);
        TextView textView=view.findViewById(R.id.video_name);

        //此处需要修改成路径
        VideoView.setImageResource(R.drawable.color1);
        textView.setText(project.toString());
        return view;

    }

}
