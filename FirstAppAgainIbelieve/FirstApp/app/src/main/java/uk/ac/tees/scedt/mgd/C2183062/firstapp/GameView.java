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

    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, frameH);
    private spriteHandler SpriteHandler;

    Bitmap tileSheet = BitmapFactory.decodeResource(getResources(), R.drawable.run);


    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();

        SpriteHandler = new spriteHandler(context, tileSheet, 115,137,7,100);



    }


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


           // spriteLoader(R.drawable.run); //Draws Flame Character
            SpriteHandler.manageCurrentFrame(true);
            //SpriteHandler.setPosition(475,1100);


            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            whereToDraw.set(xPos,yPos, xPos+frameW, yPos+frameH);


            SpriteHandler.draw(canvas);
            //canvas.drawBitmap(Sprite, frameToDraw, whereToDraw, null);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

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
