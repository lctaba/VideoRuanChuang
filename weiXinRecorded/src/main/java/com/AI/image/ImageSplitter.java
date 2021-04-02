package com.AI.image;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于分割图像
 */
public class ImageSplitter {
    /**
     * @param imagePiece 输入图片
     * @param xNum 横向的分割数量
     * @param yNum 纵向的分割数量
     * @return 切割后的图片列表
     */
    public static List<ImagePiece> split(ImagePiece imagePiece, int xNum, int yNum){
        List<ImagePiece> pieces = new ArrayList<ImagePiece>(xNum*yNum);
        Bitmap bitmap = imagePiece.getBitmap();
        if(bitmap == null){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width/xNum;
        int pieceHeight = height/yNum;
        for(int i = 0; i<xNum; i++) {
            for (int j = 0; j < yNum; j++) {
                ImagePiece piece = new ImagePiece();
                piece.setIndex(imagePiece.getIndex()*4 + i*xNum + j);
                int xStart = i * pieceWidth;
                int yStart = j * pieceHeight;
                piece.setBitmap(Bitmap.createBitmap(bitmap, xStart, yStart, pieceWidth, pieceHeight));
                pieces.add(piece);
            }
        }
        return pieces;
    }


}
