package com.AI.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.aiunit.common.protocol.gesture.GestureLandmark;
import com.aiunit.common.protocol.gesture.GestureResult;
import com.aiunit.common.protocol.gesture.GestureResultList;
import com.aiunit.common.protocol.gesture.GestureType;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.gesture.HandInputSlot;
import com.aiunit.vision.gesture.HandOutputSlot;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;

import java.util.ArrayList;
import java.util.List;


public class ImageHandler {
    private CVUnitClient mCVClient;
    private Context context;

    public ImageHandler(Context context){
        this.context = context;
    }

    public void initClient(){
        //初始化算法类
        mCVClient = CVUnit.getGestureLandmarkDetectorClient
                (context).addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
            @Override
            public void onConnectionSucceed() {
                Log.i("TAG", " authorize connect: onConnectionSucceed");
            }
        }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.e("TAG", " authorize connect: onFailure: " + connectionResult.getErrorCode());
            }
        });

        //连接AIUnit服务
        mCVClient.initService(context, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                int startCode = mCVClient.start();
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });
    }

    //释放连接
    public void closeClient(){
        if (mCVClient != null) {
            mCVClient.stop();
        }
        mCVClient.releaseService();
        mCVClient = null;
    }


    /**
     * 判断手势类型
     * @param bitmap 输入图片
     * @return 手势类型
     */
    public GestureType gestureLandmark(Bitmap bitmap){
        Log.i("start", "gestureLandmark: "+System.currentTimeMillis());
        //设置输入
        HandInputSlot inputSlot = (HandInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(bitmap);

        //设置输出
        HandOutputSlot outputSlot = (HandOutputSlot) mCVClient.createOutputSlot();

        //运行算法
        mCVClient.process(inputSlot, outputSlot);
        GestureResultList gestureResultList = outputSlot.getHandList();
        if(gestureResultList == null){
            return null;
        }
        List<GestureResult> gestureResults = gestureResultList.getList();
        for (GestureResult gestureResult : gestureResults) {
            GestureLandmark landmark = gestureResult.getLandmark();
            List<Float> lmkList =landmark.getLmk();
            GestureType result = gestureResult.getLabel();
            Log.i("result", "gestureLandmark: "+result.toString());
            return result;
        }
        Log.i("end", "gestureLandmark: "+System.currentTimeMillis());
        return null;
    }


    /**
     * 判断是否为目标手势
     * 由于识别精度不高，距离较远的手势难以识别到，因此考虑将图片分割识别
     * @param imagePiece  输入图片
     * @param rTime  递归次数
     * @return bool类型
     */
    public boolean gestureTypeTest(ImagePiece imagePiece, int rTime,GestureType gestureType) {

        List<ImagePiece> pieces = new ArrayList<ImagePiece>();
        boolean res = false;

        if (gestureLandmark(imagePiece.getBitmap()) == gestureType) {
            return true;
        }

        if(rTime<1){
             pieces = ImageSplitter.split(imagePiece, 2, 2);
            for (ImagePiece piece : pieces) {
                res = gestureTypeTest(imagePiece, rTime + 1, gestureType);
                if(res){
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * 判断是否要开启或停止录制
     * 由于识别精度不高，距离较远的手势难以识别到，因此考虑将图片分割识别
     * @param imagePiece  输入图片
     * @param rTime  递归次数
     * @return 0为什么都不做，1为停止，2为重新开始录制
     */
    public int startOrStop(ImagePiece imagePiece, int rTime) {
        Log.i("start"+rTime, "startOrStop: "+System.currentTimeMillis());
        List<ImagePiece> pieces = new ArrayList<ImagePiece>();
        int res = 0;

        if (gestureLandmark(imagePiece.getBitmap()) == GestureType.GTYPE_FIVE) {
            return 1;
        }
        if (gestureLandmark(imagePiece.getBitmap()) == GestureType.GTYPE_OK) {
            return 2;
        }

        if(rTime<1){
            pieces = ImageSplitter.split(imagePiece, 2, 2);
            for (ImagePiece piece : pieces) {
                res = startOrStop(imagePiece, rTime + 1);
                if(res != 0){
                    return res;
                }
            }
        }
        Log.i("end"+rTime, "startOrStop: "+System.currentTimeMillis());
        return 0;
    }

    /**
     * 判断是否要开启或停止录制
     * 由于识别精度不高，距离较远的手势难以识别到，因此考虑将图片分割识别
     * @param imagePiece  输入图片
     * @return 0为什么都不做，1为停止，2为重新开始录制
     */
    public int startOrStop(ImagePiece imagePiece) {
        Log.i("start", "startOrStop: "+System.currentTimeMillis());
        List<ImagePiece> pieces = new ArrayList<ImagePiece>();
        int res = 0;

        if (gestureLandmark(imagePiece.getBitmap()) == GestureType.GTYPE_FIVE) {
            return 1;
        }
        if (gestureLandmark(imagePiece.getBitmap()) == GestureType.GTYPE_OK) {
            return 2;
        }

        Log.i("end", "startOrStop: "+System.currentTimeMillis());
        return 0;
    }
}
