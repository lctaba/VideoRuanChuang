package com.AI.Audio;


import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.msp.lfasr.LfasrClient;
import com.iflytek.msp.lfasr.model.Message;
import com.projectUtil.Project;
import com.projectUtil.Subtitle;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * @Author cyh
 * @Date 2021/3/30 21:47
 */
public class AudioToText {
    public static final String HOST = "https://raasr.xfyun.cn/api";
    public static final String APP_ID = "ab825553";
    public static final String SECRET_KEY = "dc0a7a788e56d1109bc0144cbb55ff06";
    public static final String PREPARE = "/prepare";
    public static final String MERGE = "/merge";
    public static final String GET_RESULT = "/getResult";
    public static final String GET_PROGRESS = "/getProgress";

    public static final int SLICE_SIZE = 10485760;
    private Timer timer = null;
    private StringBuffer stringBuffer = null;

    public AudioToText(){
        stringBuffer = new StringBuffer();
    }

    public static Message getMessage(String path){
        //初始化客户端
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID,SECRET_KEY);
        //上传音频文件
        Message task = lfasrClient.upload(path);
        String taskId = task.getData();
        Log.i("getMessage", "taskId: "+taskId);

        //查看撰写进度
        int status = 0;
        while (status != 9){
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            Log.i("getMessage", "Message: "+message.getData());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message result = lfasrClient.getResult(taskId);
        Log.i("getMessage", "result: "+result.getData());
        JSONArray array = (JSONArray) JSON.parse(result.getData());
        Log.i("getMessage", "result: "+array);
        return result;
    }

    public static List<Subtitle> bindMessageToProject(JSONArray array){
        List<Subtitle> subtitles = new ArrayList<>();
        for(Object object : array){
            JSONObject jsonObject = (JSONObject) object;
            Subtitle subtitle = new Subtitle();
            subtitle.startTime = jsonObject.getLong("bg");
            subtitle.endTime = jsonObject.getLong("ed");
            subtitle.subtitle = jsonObject.getString("onebest");
            subtitles.add(subtitle);
        }
        return subtitles;
    }

    public static List<Subtitle> getSubtitles(String path){
        //初始化客户端
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID,SECRET_KEY);
        //上传音频文件
        Message task = lfasrClient.upload(path);
        String taskId = task.getData();
        Log.i("getMessage", "taskId: "+taskId);

        //查看撰写进度
        int status = 0;
        while (status != 9){
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            Log.i("getMessage", "Message: "+message.getData());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message result = lfasrClient.getResult(taskId);
        Log.i("getMessage", "result: "+result.getData());
        JSONArray array = (JSONArray) JSON.parse(result.getData());
        Log.i("getMessage", "result: "+array);
        return bindMessageToProject(array);
    }
}
