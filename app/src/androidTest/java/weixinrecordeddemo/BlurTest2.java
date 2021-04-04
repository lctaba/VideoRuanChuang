package weixinrecordeddemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.AI.image.OpencvUnit;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Author cyh
 * @Date 2021/3/29 14:55
 */
public class BlurTest2 {
    @Test
    public void isBlur2() throws FileNotFoundException {
        Bitmap bitmap = OpencvUnit.convertFileToBitmap("Image/img1.png");
        System.out.println(OpencvUnit.calBlurByLaplacian(bitmap));
    }

    @Test
    public void test2(){
        System.out.println(System.currentTimeMillis());
    }
}
