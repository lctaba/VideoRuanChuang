package com.zhaoss.weixinrecorded.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projectUtil.BeCutSubtitleSpan;
import com.zhaoss.weixinrecorded.R;

import java.util.List;

public class TextAdapter extends ArrayAdapter<BeCutSubtitleSpan> {
    private int resourceId;
    public TextAdapter(Context context,
                        int textViewResourceId,
                        List<BeCutSubtitleSpan> object){
        super(context,textViewResourceId,object);
        resourceId=textViewResourceId;
    }
    public View getView(int position, View coverView, ViewGroup parent) {
        BeCutSubtitleSpan beCutSubtitleSpan=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView textView=view.findViewById(R.id.video_name);
        textView.setText(beCutSubtitleSpan.toString());
        return textView;
    }
}
