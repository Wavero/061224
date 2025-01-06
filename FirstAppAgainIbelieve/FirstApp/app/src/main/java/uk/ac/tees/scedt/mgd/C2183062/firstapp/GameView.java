package uk.ac.tees.scedt.mgd.C2183062.firstapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {


    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Canvas canvas;
    private Paint scoreText;
    private Paint dangerText;

    private Context context;
    private int flamePosX = 100, flamePosY = 900;

    private int logPosX = 175,logPosY = 1700;

    private int fireScore = 0;
    private int flameFrameRate = 20;
    private int logFrameRate = 1;
    private long fps;
    private long timeThisFrame;

    private spriteHandler flameSprite,tapMeSprite,logSprite;

    Bitmap idleAnim,tappedAnim,tapme,logdefault,logCatchFire,logOnFire,helpText;

    //Background types
    private String backgroundColour;
    private String idleBackground = "#E97451",tappedBackground = "#e88a6e",dangerBackground = "#FF0000", gameWonBackground = "#008DE5" ;

    private int newRandomNumber;
    private boolean inDanger = false, isGameWon = false;

    private SensorManager manager;
    private Sensor sensor;

    private boolean dangerCheckA= false,dangerCheckB = false;
    public GameView(Context context) {
        super(context);
        this.context = context;
        surfaceHolder = getHolder();

        backgroundColour = idleBackground;

        bitmapInitialisers();
        newRandom();

        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        boolean hasAccel = !manager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty();

        sensor = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        SensorEventListener listener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                if(hasAccel) {
                    if (inDanger) {
                        if (event.values[0] < -5) {

                            dangerCheckA = true; //Checks if the phone was rotated one way
                        }
                        if (event.values[0] > 5) {
                            dangerCheckB = true;//Checks if the phone was rotated another way
                        }
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {


            }
        };
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

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
    private void update()
    {//physics

        if (fireScore % newRandomNumber == 0 && fireScore != 0)
        {
            newRandom();
            //DO a thing
            rotateMiniGame();
        }
        if (fireScore == 200){ //Game win condition
            isGameWon = true;
        }
        if (dangerCheckA && dangerCheckB) //If both checks have been done, exit mini game and resume main cookie game
        {
            inDanger = false;
            dangerCheckA = false;
            dangerCheckB = false;
            onFireReleased();
            changeLogAnim(logdefault,logPosX,logPosY,1,90);
        }

    }
    private void rotateMiniGame()
    {
        backgroundColour = dangerBackground;
        inDanger = true;
        logFrameRate = 2;
        changeLogAnim(logOnFire,logPosX,logPosY,logFrameRate,100);

    }
    private int newRandom(){

        Random random = new Random();
        newRandomNumber = random.nextInt(31) +30; //Random number from 30-60
        return newRandomNumber;

    }
    private void textInitialiser(Paint text, String textColor, int textSize, boolean isAntiAlias) //Scalable in case i want more text types
    {
        text.setColor(Color.parseColor(textColor));
        text.setTextSize(textSize);
        text.setAntiAlias(isAntiAlias);
    }

    private void draw() //graphics
    {
        if(surfaceHolder.getSurface().isValid()) {
            flameSprite.manageCurrentFrame(true);


                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.parseColor(backgroundColour)); //Brown background

            if (!isGameWon)
            {
                canvas.drawText("Score: ", 340, 1650, scoreText);
                canvas.drawText(" " + fireScore, 430, 1800, scoreText);
                flameSprite.draw(canvas);
                logSprite.draw(canvas);

                if (fireScore == 0) //Deletes the "tap me" help indicator once the player has tapped
                {
                    tapMeSprite.draw(canvas);
                }
                if (inDanger) {
                    canvas.drawText("LOG ON FIRE! ", 300, 400, dangerText);
                    canvas.drawText("ROTATE PHONE NOW ", 200, 600, dangerText);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
            if (isGameWon)
         {
               backgroundColour = gameWonBackground;
              canvas.drawText("YOU WIN",250,1200,scoreText);
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

        changeFlameAnim(tappedAnim, flamePosX+75,flamePosY-135,4,40);
        backgroundColour = tappedBackground;
        scoreText.setColor(Color.parseColor("#C4C4C4"));

    }
    private void onFireReleased()
    {
        backgroundColour = idleBackground;
        changeFlameAnim(idleAnim, flamePosX,flamePosY-200,20,80);
        scoreText.setColor(Color.parseColor("#FFFFFF"));
    }
    private void changeFlameAnim(Bitmap tileSheet,int posX, int posY, int frameRate, int frameLength)
    {
        flameSprite = new spriteHandler(context, tileSheet, tileSheet.getWidth()/frameRate,tileSheet.getHeight(),frameRate,frameLength);
        flameSprite.setPosition(posX,posY);
    }

    //Had to make another function because type spriteHandler wasn't actually changing the animation
    private void changeLogAnim(Bitmap tileSheet,int posX, int posY, int frameRate, int frameLength)

    {
        logSprite = new spriteHandler(context, tileSheet, tileSheet.getWidth()/frameRate,tileSheet.getHeight(),frameRate,frameLength);
        logSprite.setPosition(posX,posY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // touch character
                Log.e("GameView" , "location is " + event.getRawX() + " " + event.getRawY());

                if (!inDanger && !isGameWon) //In danger means the rotation mini game is started and you need to get out to click again
                {
                    if (event.getRawX() > 254 & event.getRawX() < 817 & event.getRawY() > 1064 & event.getRawY() < 1642) {
                        Log.e("GameView", "fireScore is " + fireScore);
                        onFirePressed();
                    }
                }
                break;
            case MotionEvent.ACTION_UP: //Click is lifted up
                //Might want to check to see if the button has been pressed (make bools)
                if (!inDanger && !isGameWon) {
                    onFireReleased();
                }
                break;

            default:
                break;
        }

        return true;
    }

    private void bitmapInitialisers()
    {
        idleAnim = BitmapFactory.decodeResource(getResources(), R.drawable.idleflame);
        tappedAnim = BitmapFactory.decodeResource(getResources(), R.drawable.tappedflame);

        tapme = BitmapFactory.decodeResource(getResources(), R.drawable.tapmesmall);
        logdefault = BitmapFactory.decodeResource(getResources(), R.drawable.logdef);
        logCatchFire = BitmapFactory.decodeResource(getResources(), R.drawable.logcatchfire);
        logOnFire = BitmapFactory.decodeResource(getResources(), R.drawable.logonfire);

        helpText = BitmapFactory.decodeResource(getResources(), R.drawable.helplogo);

        flameSprite = new spriteHandler(context, idleAnim, idleAnim.getWidth()/flameFrameRate,idleAnim.getHeight(),flameFrameRate,80);
        flameSprite.setPosition(flamePosX,flamePosY-200);

        tapMeSprite = new spriteHandler(context, tapme,tapme.getWidth(),tapme.getHeight(),logFrameRate,100);
        tapMeSprite.setPosition(flamePosX+tapme.getWidth() +200,flamePosY-tapme.getHeight()-200);

        scoreText= new Paint();
        textInitialiser(scoreText,"#FFFFFF",150,true);

        dangerText = new Paint();
        textInitialiser(dangerText,"#330000",75,true);

        logSprite = new spriteHandler(context, logdefault, logdefault.getWidth(),logdefault.getHeight(),1,80);
        logSprite.setPosition(logPosX,logPosY);




    }


}
