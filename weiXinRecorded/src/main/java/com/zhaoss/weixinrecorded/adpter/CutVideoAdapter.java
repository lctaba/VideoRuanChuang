package com.zhaoss.weixinrecorded.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projectUtil.BeCutErrorVideoSpan;
import com.zhaoss.weixinrecorded.R;

import java.util.List;

public class CutVideoAdapter extends ArrayAdapter<BeCutErrorVideoSpan> {
    private int resourceId;
    public CutVideoAdapter(Context context,
                       int textViewResourceId,
                       List<BeCutErrorVideoSpan> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View coverView, ViewGroup parent) {
        BeCutErrorVideoSpan beCutErrorVideoSpan=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView textView=view.findViewById(R.id.video_name);
        textView.setText(beCutErrorVideoSpan.toString());
        return view;
    }
}