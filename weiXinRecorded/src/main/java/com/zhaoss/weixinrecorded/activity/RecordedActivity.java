  package com.zhaoss.weixinrecorded.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AI.image.ImageHandler;
import com.AI.image.ImagePiece;
import com.AI.image.OpencvUnit;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.libyuv.LibyuvUtil;
import com.projectUtil.ErrorType;
import com.projectUtil.ErrorVideo;
import com.projectUtil.Project;
import com.projectUtil.ProjectUtil;
import com.projectUtil.Video;
import com.projectUtil.VideoClip;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.util.CameraHelp;
import com.zhaoss.weixinrecorded.util.MyVideoEditor;
import com.zhaoss.weixinrecorded.util.RecordUtil;
import com.zhaoss.weixinrecorded.util.RxJavaUtil;
import com.zhaoss.weixinrecorded.util.Utils;
import com.zhaoss.weixinrecorded.view.LineProgressView;
import com.zhaoss.weixinrecorded.view.RecordView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 仿微信录制视频
 * 基于ffmpeg视频编译
 * Created by zhaoshuang on 19/6/18.
 */
public class RecordedActivity extends BaseActivity {

    public static final String INTENT_PATH = "intent_path";
    public static final String INTENT_DATA_TYPE = "result_data_type";

    public static final int RESULT_TYPE_VIDEO = 1;
    public static final int RESULT_TYPE_PHOTO = 2;

    public static final int REQUEST_CODE_KEY = 100;

    public static final float MAX_VIDEO_TIME = 10f*10000;  //最大录制时间
    public static final float MIN_VIDEO_TIME = 2f*1000;  //最小录制时间

    private SurfaceView surfaceView;
    private RecordView recordView;
    private ImageView iv_delete;
    private ImageView iv_next;
    private ImageView iv_change_camera;
    private LineProgressView lineProgressView;
    private ImageView iv_flash_video;
    private TextView editorTextView;
    private TextView tv_hint;

    private ArrayList<String> segmentList = new ArrayList<>();//分段视频地址
    private ArrayList<String> aacList = new ArrayList<>();//分段音频地址
    private ArrayList<Long> timeList = new ArrayList<>();//分段录制时间

    //是否在录制视频
    private AtomicBoolean isRecordVideo = new AtomicBoolean(false);
    //拍照
    private AtomicBoolean isShotPhoto = new AtomicBoolean(false);
    private CameraHelp mCameraHelp = new CameraHelp();
    private SurfaceHolder mSurfaceHolder;
    private MyVideoEditor mVideoEditor = new MyVideoEditor();
    private RecordUtil recordUtil;

    private int executeCount;//总编译次数
    private float executeProgress;//编译进度
    private String audioPath;
    private RecordUtil.OnPreviewFrameListener mOnPreviewFrameListener;

    private ImageHandler imageHandler;

    //当前录制的视频片段
    private VideoClip videoClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recorded);


        LanSoEditor.initSDK(this, null);
        /**
         * 这边还需要传入一个项目名的参数
         */
        LanSongFileUtil.setFileDir(this.getExternalFilesDir(null).getPath()+"/"+ getIntent().getStringExtra("fileName")+"/");
        /**
         * 初始化so库
         */
        LibyuvUtil.loadLibrary();

        initUI();
        initData();
        initMediaRecorder();
        videoClip = new VideoClip();
        videoClip.startTime = 0L;
        videoClip.relativeStartTime = 0L;

        /**
         * 图像处理库
         */
        imageHandler = new ImageHandler(this);
        imageHandler.initClient();
    }

    private void initUI() {

        surfaceView = findViewById(R.id.surfaceView);
        recordView = findViewById(R.id.recordView);
        iv_delete = findViewById(R.id.iv_delete);
        iv_next = findViewById(R.id.iv_next);
        iv_flash_video = findViewById(R.id.iv_flash_video);
        iv_change_camera = findViewById(R.id.iv_camera_mode);
        lineProgressView =  findViewById(R.id.lineProgressView);
        tv_hint = findViewById(R.id.tv_hint);

        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                int width = surfaceView.getWidth();
                int height = surfaceView.getHeight();
                float viewRatio = width*1f/height;
                float videoRatio = 9f/16f;
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                if(viewRatio > videoRatio){
                    layoutParams.width = width;
                    layoutParams.height = (int) (width/viewRatio);
                }else{
                    layoutParams.width = (int) (height*viewRatio);
                    layoutParams.height = height;
                }
                surfaceView.setLayoutParams(layoutParams);
            }
        });
    }


    private AtomicInteger frameTime = new AtomicInteger();
    private AtomicInteger recognitionTime = new AtomicInteger();
    private AtomicInteger gestureType = new AtomicInteger();
    private void initMediaRecorder() {
        mCameraHelp.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if(isShotPhoto.get()){
                    isShotPhoto.set(false);
                    shotPhoto(data);
                }else{
                    if(isRecordVideo.get() && mOnPreviewFrameListener!=null){
                        mOnPreviewFrameListener.onPreviewFrame(data);
                        frameTime.addAndGet(1);
                        if(frameTime.get() >= 12){
                            frameTime.set(0);
                            new Thread(new GestureThread(data)).start();
                            new Thread(new BlurThread(data)).start();
                        }
                    }else if(!isRecordVideo.get() && mOnPreviewFrameListener!=null){
                        mOnPreviewFrameListener.onPreviewFrame(data);
                        frameTime.addAndGet(1);
                        if(frameTime.get() >= 12){
                            frameTime.set(0);
                            new Thread(new GestureThread(data)).start();
                        }
                    }
                }
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_BACK, mSurfaceHolder);
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraHelp.release();
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelp.callFocusMode();
            }
        });

        mVideoEditor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
                if(percent==100){
                    executeProgress++;
                }
                int pro = (int) (executeProgress/executeCount*100);
                editorTextView.setText("视频编辑中"+pro+"%");
            }
        });
    }

    AtomicInteger blurTimes = new AtomicInteger();
    AtomicBoolean isInBlurTime = new AtomicBoolean(false);
    AtomicLong blurStartTime = new AtomicLong();
    AtomicLong blurEndTime = new AtomicLong();
    class BlurThread implements Runnable{
        private final byte[] data;
        BlurThread(final byte[] data){
            this.data = data;
        }
        @Override
        public void run() {
            Bitmap bitmap = getBitmapFromPreview(data);
            Log.i("blur", "recognize blur");
            if(OpencvUnit.isBlurByLaplacian(bitmap)){
                Log.i("blur", "is blur");
                blurTimes.addAndGet(1);
                if(blurTimes.get()>=2){
                    long countTime = videoDuration;
                    for(Long time : timeList){
                        countTime+=time;
                    }
                    if(!isInBlurTime.get()){
                        blurStartTime.set(countTime);
                    }
                    isInBlurTime.set(true);
                    Log.i("blur", "is really blur");
                }
            }else {
                Log.i("blur", "is not blur");
                if(isInBlurTime.get()){
                    isInBlurTime.set(false);
                    long countTime = videoDuration;
                    for(Long time : timeList){
                        countTime+=time;
                    }
                    blurEndTime.set(countTime);
                    videoClip.errorVideos.add(new ErrorVideo(blurStartTime.get(),blurEndTime.get(), ErrorType.PICTURE_ERROR));
                }
                blurTimes.set(0);
            }
        }
    }

    AtomicBoolean isStart = new AtomicBoolean(false);
    class GestureThread implements Runnable{
        private final byte[] data;
        GestureThread(final byte[] data){
            this.data = data;
        }
        @Override
        public void run() {
            Bitmap bitmap = getBitmapFromPreview(data);
            try{
                int gType = imageHandler.startOrStop(new ImagePiece(bitmap));
                synchronized(GestureThread.class) {
                    if(gType == gestureType.get() && gType!=0){
                        recognitionTime.addAndGet(1);
                    }else {
                        gestureType.set(gType);
                        recognitionTime.set(0);
                    }
                    if(recognitionTime.get()>=3){
                        //Toast.makeText(getApplicationContext(),gestureType.toString(),Toast.LENGTH_SHORT).show();
                        if(gType == 1){
                            isStart.set(false);
                            isRecordVideo.set(false);
                            upEvent();
                            Log.i("", "run: 1");
                        }else if(gType == 2){
                            if(!isStart.get()){
                                isStart.set(true);
                                isRecordVideo.set(true);
                                startRecord();
                                goneRecordLayout();
                                Log.i("", "run: 2");
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 从预览界面获取bitmap
     * @return
     */
    public Bitmap getBitmapFromPreview(final byte[] data) {
        boolean isFrontCamera = mCameraHelp.getCameraId()== Camera.CameraInfo.CAMERA_FACING_FRONT;
        int rotation;
        if(isFrontCamera){
            rotation = 270;
        }else{
            rotation = 90;
        }

        byte[] yuvI420 = new byte[data.length];
        byte[] tempYuvI420 = new byte[data.length];

        int videoWidth =  mCameraHelp.getHeight();
        int videoHeight =  mCameraHelp.getWidth();

        LibyuvUtil.convertNV21ToI420(data, yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight());
        LibyuvUtil.compressI420(yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight(), tempYuvI420,
                mCameraHelp.getWidth(), mCameraHelp.getHeight(), rotation, isFrontCamera);

        Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);

        LibyuvUtil.convertI420ToBitmap(tempYuvI420, bitmap, videoWidth, videoHeight);
        return bitmap;
    }

    /**
     * 处理手势识别的线程
     */
    private void gestureRecognition(final byte[] data){
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<Void>() {

            @Override
            public Void doInBackground() throws Throwable {
                boolean isFrontCamera = mCameraHelp.getCameraId()== Camera.CameraInfo.CAMERA_FACING_FRONT;
                int rotation;
                if(isFrontCamera){
                    rotation = 270;
                }else{
                    rotation = 90;
                }

                byte[] yuvI420 = new byte[data.length];
                byte[] tempYuvI420 = new byte[data.length];

                int videoWidth =  mCameraHelp.getHeight();
                int videoHeight =  mCameraHelp.getWidth();

                LibyuvUtil.convertNV21ToI420(data, yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight());
                LibyuvUtil.compressI420(yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight(), tempYuvI420,
                        mCameraHelp.getWidth(), mCameraHelp.getHeight(), rotation, isFrontCamera);

                Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);

                LibyuvUtil.convertI420ToBitmap(tempYuvI420, bitmap, videoWidth, videoHeight);

                if(imageHandler.startOrStop(new ImagePiece(bitmap)) != 0){
                    Toast.makeText(getApplicationContext(),"停止录像",Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            public void onFinish(Void result) {

            }

            @Override
            public void onError(Throwable e) {
                //Toast.makeText(getApplicationContext(), "手势识别失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shotPhoto(final byte[] nv21){

        TextView textView = showProgressDialog();
        textView.setText("图片截取中");
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {
            @Override
            public String doInBackground() throws Throwable {

                boolean isFrontCamera = mCameraHelp.getCameraId()== Camera.CameraInfo.CAMERA_FACING_FRONT;
                int rotation;
                if(isFrontCamera){
                    rotation = 270;
                }else{
                    rotation = 90;
                }

                byte[] yuvI420 = new byte[nv21.length];
                byte[] tempYuvI420 = new byte[nv21.length];

                int videoWidth =  mCameraHelp.getHeight();
                int videoHeight =  mCameraHelp.getWidth();

                LibyuvUtil.convertNV21ToI420(nv21, yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight());
                LibyuvUtil.compressI420(yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight(), tempYuvI420,
                        mCameraHelp.getWidth(), mCameraHelp.getHeight(), rotation, isFrontCamera);

                Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);

                LibyuvUtil.convertI420ToBitmap(tempYuvI420, bitmap, videoWidth, videoHeight);

                String photoPath = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".jpeg";
                FileOutputStream fos = new FileOutputStream(photoPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                return photoPath;
            }
            @Override
            public void onFinish(String result) {
                closeProgressDialog();

                Intent intent = new Intent();
                intent.putExtra(INTENT_PATH, result);
                intent.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_PHOTO);
                setResult(RESULT_OK, intent);
                finish();
            }
            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(getApplicationContext(), "图片截取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {

        lineProgressView.setMinProgress(MIN_VIDEO_TIME / MAX_VIDEO_TIME);
        recordView.setOnGestureListener(new RecordView.OnGestureListener() {
            @Override
            public void onDown() {
                /**
                //长按录像
                isRecordVideo.set(true);
                startRecord();
                goneRecordLayout();
                 */
            }
            @Override
            public void onUp() {
                /**
                if(isRecordVideo.get()){
                    isRecordVideo.set(false);
                    upEvent();
                }
                 */
            }
            @Override
            public void onClick() {
                /**
                if(segmentList.size() == 0){
                    isShotPhoto.set(true);
                }
                 */
                if(isRecordVideo.get()){
                    isRecordVideo.set(false);
                    upEvent();
                }else {
                    isRecordVideo.set(true);
                    startRecord();
                    goneRecordLayout();
                }
            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSegment();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorTextView = showProgressDialog();
                executeCount = segmentList.size()+4;
                finishVideo();
            }
        });

        iv_flash_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelp.changeFlash();
                if (mCameraHelp.isFlashOpen()) {
                    iv_flash_video.setImageResource(R.mipmap.video_flash_open);
                } else {
                    iv_flash_video.setImageResource(R.mipmap.video_flash_close);
                }
            }
        });

        iv_change_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCameraHelp.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK){
                    mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_FRONT, mSurfaceHolder);
                }else{
                    mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_BACK, mSurfaceHolder);
                }
                iv_flash_video.setImageResource(R.mipmap.video_flash_close);
            }
        });
    }

    String storeH264Path;
    String storeMp4Path;
    String storeAacPath;
    public void finishVideo(){
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {
            @Override
            public String doInBackground()throws Exception{
                //合并h264
                storeH264Path = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".h264";
                String h264Path = storeH264Path;
                Utils.mergeFile(segmentList.toArray(new String[]{}), h264Path);
                //h264转mp4
                storeMp4Path = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".mp4";
                String mp4Path = storeMp4Path;
                mVideoEditor.h264ToMp4(h264Path, mp4Path);
                //合成音频
                storeAacPath = mVideoEditor.executePcmEncodeAac(syntPcm(), RecordUtil.sampleRateInHz, RecordUtil.channelCount);
                String aacPath = storeAacPath;
                //音视频混合
                mp4Path = mVideoEditor.executeVideoMergeAudio(mp4Path, aacPath);
                //裁剪只剩0-5秒
                //mp4Path = mVideoEditor.executeCutVideoExact(mp4Path, 0, 5);
                return mp4Path;

            }
            @Override
            public void onFinish(String result) {
                closeProgressDialog();
                Intent intent = new Intent(mContext, EditVideoActivity.class);
                //将项目的信息序列化储存
                Project project = new Project();
                project.name = getIntent().getStringExtra("fileName");
                Video video = new Video();
                video.h264Path = storeH264Path;
                video.aacPath = storeAacPath;
                video.mp4Path = storeMp4Path;
                project.videos.add(video);
                Long countTime = 0L;
                for (Long time : timeList){
                    countTime+=time;
                }
                videoClip.endTime = countTime;
                videoClip.relativeEndTime = countTime;
                videoClip.belongTo = video;
                project.videoClips.add(videoClip);
                ProjectUtil.storeProject(LanSongFileUtil.DEFAULT_DIR,project);
                intent.putExtra(INTENT_PATH, result);
                intent.putExtra("project",project);
                startActivityForResult(intent, REQUEST_CODE_KEY);
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                closeProgressDialog();
                Toast.makeText(getApplicationContext(), "视频编辑失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String syntPcm() throws Exception{

        String pcmPath = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".pcm";
        File file = new File(pcmPath);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        for (int x=0; x<aacList.size(); x++){
            FileInputStream in = new FileInputStream(aacList.get(x));
            byte[] buf = new byte[4096];
            int len=0;
            while ((len=in.read(buf))>0){
                out.write(buf, 0, len);
                out.flush();
            }
            in.close();
        }
        out.close();
        return pcmPath;
    }

    private void goneRecordLayout(){

        tv_hint.setVisibility(View.GONE);
        iv_delete.setVisibility(View.GONE);
        iv_next.setVisibility(View.GONE);
    }

    private volatile long videoDuration;
    private long recordTime;
    private String videoPath;
    private void startRecord(){

        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                videoPath = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".h264";
                audioPath = LanSongFileUtil.DEFAULT_DIR+System.currentTimeMillis()+".pcm";
                final boolean isFrontCamera = mCameraHelp.getCameraId()== Camera.CameraInfo.CAMERA_FACING_FRONT;
                final int rotation;
                if(isFrontCamera){
                    rotation = 270;
                }else{
                    rotation = 90;
                }
                recordUtil = new RecordUtil(videoPath, audioPath, mCameraHelp.getWidth(), mCameraHelp.getHeight(), rotation, isFrontCamera);
                return true;
            }
            @Override
            public void onFinish(Boolean result) {
                if(isRecordVideo.get()){
                    mOnPreviewFrameListener = recordUtil.start();
                    videoDuration = 0;
                    lineProgressView.setSplit();
                    recordTime = System.currentTimeMillis();
                    runLoopPro();
                }else{
                    recordUtil.release();
                    recordUtil = null;
                }
            }
            @Override
            public void onError(Throwable e) {

            }
        });
    }

    private void runLoopPro(){

        RxJavaUtil.loop(20, new RxJavaUtil.OnRxLoopListener() {
            @Override
            public Boolean takeWhile(){
                return recordUtil!=null && recordUtil.isRecording();
            }
            @Override
            public void onExecute() {
                long currentTime = System.currentTimeMillis();
                videoDuration += currentTime - recordTime;
                recordTime = currentTime;
                long countTime = videoDuration;
                for (long time : timeList) {
                    countTime += time;
                }
                if (countTime <= MAX_VIDEO_TIME) {
                    lineProgressView.setProgress(countTime/ MAX_VIDEO_TIME);
                }else{
                    upEvent();
                    iv_next.callOnClick();
                }
            }
            @Override
            public void onFinish() {
                segmentList.add(videoPath);
                aacList.add(audioPath);
                timeList.add(videoDuration);
                initRecorderState();
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                lineProgressView.removeSplit();
            }
        });
    }

    private void upEvent(){
        if(recordUtil != null) {
            recordUtil.stop();
            recordUtil = null;
        }
        initRecorderState();
    }

    private void deleteSegment(){

        showConfirm("确认删除上一段视频?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeProgressDialog();

                if(segmentList.size()>0 && timeList.size()>0) {
                    segmentList.remove(segmentList.size() - 1);
                    aacList.remove(aacList.size() - 1);
                    timeList.remove(timeList.size() - 1);
                    lineProgressView.removeSplit();
                }
                initRecorderState();
            }
        });
    }

    /**
     * 初始化视频拍摄状态
     */
    private void initRecorderState(){

        if(segmentList.size() > 0){
            tv_hint.setText("长按录像");
        }else{
            tv_hint.setText("长按录像 点击拍照");
        }
        tv_hint.setVisibility(View.VISIBLE);

        if (lineProgressView.getSplitCount() > 0) {
            iv_delete.setVisibility(View.VISIBLE);
        }else{
            iv_delete.setVisibility(View.GONE);
        }

        /**
        if (lineProgressView.getProgress()* MAX_VIDEO_TIME < MIN_VIDEO_TIME) {
            iv_next.setVisibility(View.GONE);
        } else {
            iv_next.setVisibility(View.VISIBLE);
        }
         */
        if(isRecordVideo.get()){
            iv_next.setVisibility(View.GONE);
        }else {
            iv_next.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 清除录制信息
     */
    private void cleanRecord(){

        recordView.initState();
        lineProgressView.cleanSplit();
        segmentList.clear();
        aacList.clear();
        timeList.clear();

        executeCount = 0;
        executeProgress = 0;

        iv_delete.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.INVISIBLE);
        iv_flash_video.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cleanRecord();
        if(mCameraHelp != null){
            mCameraHelp.release();
        }
        if(recordUtil != null) {
            recordUtil.stop();
        }
        if(imageHandler != null){
            imageHandler.closeClient();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && data!=null){
            if(requestCode == REQUEST_CODE_KEY){
                Intent intent = new Intent();
                intent.putExtra(INTENT_PATH, data.getStringExtra(INTENT_PATH));
                intent.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
                setResult(RESULT_OK, intent);
                finish();
            }
        }else{
            cleanRecord();
            initRecorderState();
        }
    }
}
