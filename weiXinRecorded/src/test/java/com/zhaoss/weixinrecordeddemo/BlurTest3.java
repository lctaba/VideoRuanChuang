package com.zhaoss.weixinrecordeddemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.AI.image.OpencvUnit;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Author cyh
 * @Date 2021/3/29 14:55
 */
public class BlurTest3 {
    @Test
    public void isBlur3() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("D:/javaproject/android/VideoRuanChuang/Image/img1.png");
        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
        System.out.println(OpencvUnit.calBlurByLaplacian(bitmap));
    }

    @Test
    public void test3(){
        System.out.println(System.currentTimeMillis());
        System.out.println(System.getProperty("java.library.path"));
    }
}
