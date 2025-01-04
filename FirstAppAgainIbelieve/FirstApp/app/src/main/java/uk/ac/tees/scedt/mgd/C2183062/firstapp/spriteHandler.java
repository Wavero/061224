package uk.ac.tees.scedt.mgd.C2183062.firstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Canvas;

public class spriteHandler {
    private Context context;

    private Bitmap tileSheet;
    private int frameW;
    private int frameH;
    private int frameCount;
    private int currentFrame = 0;
    private long frameLengthinMS;
    private long lastFrameChangeTime = 0;

    private Rect frameToDraw;
    private RectF whereToDraw;
    public spriteHandler(Context context, Bitmap tileSheet, int frameW, int frameH, int frameCount, long frameLengthInMS){


        this.context = context;
        this.tileSheet = tileSheet;
        this.frameW = frameW;
        this.frameH = frameH;
        this.frameCount = frameCount;
        this.frameLengthinMS = frameLengthInMS;

        this.frameToDraw = new Rect(0,0,frameW,frameH);
        this.whereToDraw = new RectF(0,0,frameW,frameH);


    }

    public void manageCurrentFrame(boolean doAnim){
        long time = System.currentTimeMillis();
        if (doAnim){
            if (time > lastFrameChangeTime + frameLengthinMS){
                lastFrameChangeTime = time;
                currentFrame++;

                if (currentFrame >= frameCount)
                {
                    currentFrame = 0;
                }
            }
        }
        frameToDraw.left = currentFrame*frameW;
        frameToDraw.right = frameToDraw.left + frameW;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameH;
    }

    public void setPosition(float xPos, float yPos) {
        whereToDraw.left = xPos;
        whereToDraw.top = yPos;
        whereToDraw.right = xPos + frameW;
        whereToDraw.bottom = yPos + frameH;
    }
    public Bitmap loadSprite(int resourceID){

        return BitmapFactory.decodeResource(context.getResources(), resourceID);

    }

    public Bitmap scaleSprite(Bitmap bitmap, float scaleW, float scaleH){ //Code to change sprite size
        int width = (int) (bitmap.getWidth()* scaleW);
        int height = (int) (bitmap.getHeight()* scaleH);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public void draw(Canvas canvas){
        if (tileSheet != null) {
            canvas.drawBitmap(tileSheet, frameToDraw, whereToDraw, null);
        }
    }


}

