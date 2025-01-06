package uk.ac.tees.scedt.mgd.C2183062.firstapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private Paint scoreText;

    private Context context;
    private int flamePosX = 100, flamePosY = 900;

    private int fireScore = 0;
    private int flameFrameRate = 20;
    private long fps;
    private long timeThisFrame;

    private spriteHandler SpriteHandler;

    Bitmap idleAnim = BitmapFactory.decodeResource(getResources(), R.drawable.idleflame);
    Bitmap tappedAnim = BitmapFactory.decodeResource(getResources(), R.drawable.tappedflame);

    //Background types
    private String backgroundColour;
    private String idleBackground = "#E97451";
    private String tappedBackground = "#e88a6e";



    public GameView(Context context) {
        super(context);
        this.context = context;
        surfaceHolder = getHolder();

        backgroundColour = idleBackground;

        SpriteHandler = new spriteHandler(context, idleAnim, idleAnim.getWidth()/flameFrameRate,idleAnim.getHeight(),flameFrameRate,80);
        SpriteHandler.setPosition(flamePosX,flamePosY);

        scoreText= new Paint();
        textInitialiser(scoreText,"#FFFFFF",150,true);

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

    private void textInitialiser(Paint text, String textColor, int textSize, boolean isAntiAlias)
    {
        text.setColor(Color.parseColor(textColor));
        text.setTextSize(textSize);
        text.setAntiAlias(isAntiAlias);
    }

    private void draw() //graphics
    {
        if(surfaceHolder.getSurface().isValid())
        {

            SpriteHandler.manageCurrentFrame(true);

            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor(backgroundColour)); //Brown background
            canvas.drawText("Score: ",340,1850,scoreText);
            canvas.drawText(" " + fireScore,430,2000,scoreText);
            SpriteHandler.draw(canvas);

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

    private void onFirePressed()
    {
        fireScore = fireScore + 1;

        changeFlameAnim(tappedAnim, flamePosX+75,flamePosY+75,4,40);
        backgroundColour = tappedBackground;
        scoreText.setColor(Color.parseColor("#C4C4C4"));
    }
    private void onFireReleased()
    {
        backgroundColour = idleBackground;
        changeFlameAnim(idleAnim, flamePosX,flamePosY,20,80);
        scoreText.setColor(Color.parseColor("#FFFFFF"));
        return;
    }

    private void changeFlameAnim(Bitmap tileSheet,int posX, int posY, int frameRate, int frameLength)
    {
        SpriteHandler = new spriteHandler(context, tileSheet, tileSheet.getWidth()/frameRate,tileSheet.getHeight(),frameRate,frameLength);
        SpriteHandler.setPosition(posX,posY);
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
                if (event.getRawX() > 254 & event.getRawX() < 817 & event.getRawY() > 1064 & event.getRawY() < 1642)
                {

                    Log.e("GameView" , "fireScore is " + fireScore);
                    onFirePressed();

                }

                break;
            case MotionEvent.ACTION_UP: //Click is lifted up
                onFireReleased();

                break;

            default:
                break;
        }

        return true;
    }




}
