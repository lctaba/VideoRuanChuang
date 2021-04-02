package com.AI.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.bytedeco.javacpp.opencv_core.BORDER_DEFAULT;
import static org.bytedeco.javacpp.opencv_core.CV_16U;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.mean;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.Laplacian;
import static org.bytedeco.javacpp.opencv_imgproc.Sobel;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;


/**
 * @Author cyh+
 * @Date 2021/3/29 14:24
 */
public class OpencvUnit {
    /**
     * 对原图像梯度化，计算梯度值，梯度值越高，图片越清晰
     * @param bitmap 输入的图片
     * @return
     */

    public static double culBlurByTenengrad(Bitmap bitmap){
        Log.i("TAG", "culBlurByTenengrad: begin");

        //灰度化
        Mat matImageGrey = convertBitmapToGrey(bitmap);
        Mat imageSobel = new Mat();

        //Sobel算子计算tenengrad梯度
        Sobel(matImageGrey,imageSobel, CV_16U,1,1);

        Log.i("TAG", "culBlurByTenengrad: end");
        //计算图像的平均灰度
        return mean(imageSobel).get(0);
    }


    /**
     * 先对图像灰度化，用3*3的拉普拉斯算子进行滤波处理，在计算梯度平均值
     * @param bitmap
     * @return
     */

    public static double culBlurByLaplacian(Bitmap bitmap){
        Log.i("TAG", "calBlurByLaplacian: begin");

        //灰度化
        Mat matImageGrey = convertBitmapToGrey(bitmap);
        Mat imageSobel = new Mat();
        Laplacian(matImageGrey,imageSobel, CV_16U,3,1,0,BORDER_DEFAULT);

        Log.i("TAG", "calBlurByLaplacian: end");
        return mean(imageSobel).get(0);
    }


    /*private static Mat convertBitmapToGrey(Bitmap bitmap) {
        //转为mat矩阵
        IplImage iplImage = bitmapToIplImage(bitmap);
        Mat matImage = new Mat(iplImage);

        //图像灰度化
        Mat matImageGrey = new Mat();
        if(matImage.channels() != 1){ //1表示单通道
            cvtColor(matImage,matImageGrey, opencv_imgproc.COLOR_BGR2GRAY);
        }else {  //如果是1，表示本来就是单通道图片，即本来就是灰度图片
            matImageGrey = matImage.clone();
        }
        return matImageGrey;
    }*/
    private static Mat convertBitmapToGrey(Bitmap bitmap) {
        //转为mat矩阵
        IplImage iplImage = bitmapToIplImage(bitmap);
        Mat matImage = new Mat(iplImage);

        //图像灰度化
        Mat matImageGrey = new Mat();
        if(matImage.channels() != 1){ //1表示单通道
            cvtColor(matImage,matImageGrey, COLOR_BGR2GRAY);
        }else {  //如果是1，表示本来就是单通道图片，即本来就是灰度图片
            matImageGrey = matImage.clone();
        }
        return matImageGrey;
    }
    /**
     * 将bitmap转为IplImage
     * @param bitmap
     * @return
     */
    public static IplImage bitmapToIplImage(Bitmap bitmap) {
        IplImage iplImage;
        iplImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
                IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
        return iplImage;
    }


    /**
     * 大于1是清晰图片，小于1是模糊图片
     * @param bitmap
     * @return
     */

    public static boolean isBlurByTenengrad(Bitmap bitmap){
        Log.i("TAG", "isBlurByLaplacian: begin");
        double result = culBlurByTenengrad(bitmap);
        Log.i("TAG", "isBlurByLaplacian: "+result);
        Log.i("TAG", "isBlurByLaplacian: end");
        if(result > 3){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 大于3是清晰图片，小于3是模糊图片
     * @param bitmap
     * @return
     */

    public static boolean isBlurByLaplacian(Bitmap bitmap){
        Log.i("TAG", "isBlurByLaplacian: begin");
        double result = culBlurByLaplacian(bitmap);
        Log.i("TAG", "isBlurByLaplacian: "+result);
        Log.i("TAG", "isBlurByLaplacian: end");
        if(result > 3){
            return false;
        }else {
            return true;
        }
    }

    public static Bitmap convertFileToBitmap(String fileName) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        return BitmapFactory.decodeStream(fis);
    }

}
