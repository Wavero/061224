package uk.ac.tees.scedt.mgd.C2183062.firstapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {


    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Canvas canvas;
    private Bitmap bitmap;
    private boolean isMoving;
    private float xPos = 475, yPos = 1100;

    private int frameW = 115, frameH = 137;
    private int frameCount = 8;
    private int currentFrame = 0;
    private int fireScore = 0;

    boolean doAnimation;
    private long fps;
    private long timeThisFrame;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, frameH);



public void spriteLoader(int ResourceID)
{
    bitmap = BitmapFactory.decodeResource(getResources(), ResourceID); //run is the png image of whatever animation
    bitmap = Bitmap.createScaledBitmap(bitmap, frameW * frameCount, frameH, false);
}

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();


    }

    //make sprite class, do constructor stuff


    @Override
    public void run(){
        while (playing)
        {
            long startFrameTime = System.currentTimeMillis();
            update(); //physics
            draw();   //graphics
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1)
            {
                fps = 1000/timeThisFrame;
            }
        }
    }

    private void update() //physics
    {

    }
    private void draw() //graphics
    {
        if(surfaceHolder.getSurface().isValid())
        {
            spriteLoader(R.drawable.run); //Draws Flame Character

            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            whereToDraw.set(xPos,yPos, xPos+frameW, yPos+frameH);
            manageCurrentFrame(doAnimation);
            canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
    public void manageCurrentFrame(boolean doAnim) //current frame of animation
    {
        long time = System.currentTimeMillis();
        if (doAnim)
        {
            if (time > lastFrameChangeTime + frameLengthInMS)
            {
                lastFrameChangeTime = time;
                currentFrame++;

                if (currentFrame >= frameCount)
                {
                    currentFrame = 0;
                }
            }
        }
        frameToDraw.left = currentFrame * frameW;
        frameToDraw.right = frameToDraw.left + frameW;


    }
    public void pause() //pause screen
    {
        playing = false;
        try {
            gameThread.join();
        } catch(InterruptedException e)
        {
            Log.e("GameView" , "Interrupted");
        }
    }
    public void resume() //resume screen
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int pointerIndex = event.getActionIndex();
        int pointerID = event.getPointerId(pointerIndex);

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // touch character
                Log.e("GameView" , "location is " + event.getRawX() + " " + event.getRawY());
                if (event.getRawX() > xPos & event.getRawX() < xPos + frameW & event.getRawY() > yPos & event.getRawY() < yPos + frameH)
                {
                    fireScore = fireScore + 1;
                    Log.e("GameView" , "fireScore is " + fireScore);

                }
                if (doAnimation )
                {
                    doAnimation = false; //Ends Animation
                }
                else
                    {doAnimation = true;} //Starts Animation



                break;
            case MotionEvent.ACTION_UP: //Click is lifted up
                //isMoving = false;
                //isMoving = true;
                break;


            //case MotionEvent.ACTION_MOVE: //Drags the sprite wherever
              //  isMoving = false;
                //xPos = event.getX(pointerIndex) - frameH/2 ;
                //yPos = event.getY(pointerIndex) - frameW/2;
                //break;



            default:
                break;
        }

        return true;
    }




}
