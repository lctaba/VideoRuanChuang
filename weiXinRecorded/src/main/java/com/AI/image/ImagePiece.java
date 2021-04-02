package com.AI.image;

import android.graphics.Bitmap;


public class ImagePiece {
    public ImagePiece(){
        index = 0;
        bitmap = null;
    }

    public ImagePiece(Bitmap bitmap,int index){
        this.bitmap = bitmap;
        this.index = index;
    }

    public ImagePiece(Bitmap bitmap){
        this.bitmap = bitmap;
        index = 0;
    }

    public int getIndex() {
        return index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private int index;
    private Bitmap bitmap;
}
