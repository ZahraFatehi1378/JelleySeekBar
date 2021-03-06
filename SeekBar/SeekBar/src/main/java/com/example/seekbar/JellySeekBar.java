package com.example.seekbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * this class represents a view for seek bar
 * there are some methods to set animation times , colors , font , range , ...
 * author = zahra fatehi
 */

public class JellySeekBar extends View {

    private final Paint paint = new Paint();
    private final Paint paint2 = new Paint();
    private final Paint txtPaint = new Paint();
    private float chosenX;
    private int startRange, endRange;
    private float goUp, goDown;
    private boolean up = true;
    private int circleColor = Color.parseColor("#adcbe3");
    private int mainColor = Color.parseColor("#4b86b4");
    private int signColor = Color.parseColor("#011f4b");
    private final float margin = 50;
    private int borderGoUp;
    private int borderGoDown;
    private final ArrayList<Bubble> bubbles;
    private final Random random = new Random();
    private int signFirstLocation;
    private int flag = 0;
    private SeekBarLocation seekBarLocation;
    private long signDuration = 400, bubblesDuration = 600;


    public JellySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(mainColor);

        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(10);

        bubbles = new ArrayList<>();

        txtPaint.setTextSize(40);

        txtPaint.setTypeface(ResourcesCompat.getFont(context, R.font.dolce_vita));
        txtPaint.setTextAlign(Paint.Align.CENTER);

        if (startRange == endRange)
            endRange = 100;
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JellySeekBar, 0, 0);
        try {
            startRange = ta.getInt(R.styleable.JellySeekBar_start_range, 0);
            endRange = ta.getInt(R.styleable.JellySeekBar_end_rage, 0);
            signFirstLocation = ta.getInt(R.styleable.JellySeekBar_sign_first_location, 0);

            circleColor = ta.getColor(R.styleable.JellySeekBar_circle_color, 0xffadcbe3);
            mainColor = ta.getColor(R.styleable.JellySeekBar_main_color, 0xff4b86b4);
            signColor = ta.getColor(R.styleable.JellySeekBar_sign_txt_color, 0xff011f4b);

            signDuration = ta.getInt(R.styleable.JellySeekBar_sign_Duration, 400);
            bubblesDuration = ta.getInt(R.styleable.JellySeekBar_bubbles_Duration, 600);

            txtPaint.setTypeface(ResourcesCompat.getFont(context, ta.getResourceId(R.styleable.JellySeekBar_font, R.font.dolce_vita)));

        } finally {
            ta.recycle();
            invalidate();
            requestLayout();
        }
    }


    @SuppressLint({"ResourceAsColor", "DrawAllocation"})
    @Override
    protected void onDraw(Canvas canvas) {

        if (flag == 0) {// just run for first time
            chosenX = (margin + 53) + ((float) (signFirstLocation - startRange) / (endRange - startRange)) * (getWidth() - 2 * (margin + 53));
            flag = 1;
        }
        paint2.setColor(mainColor);
        paint.setColor(mainColor);
        txtPaint.setColor(signColor);

        canvas.drawRoundRect(new RectF(margin - 8, (getHeight() >> 1) - 20, getWidth() - margin + 8, (getHeight() >> 1) + 20), 100, 100, paint);


        updateBubbles();
        drawBubble(canvas);

        if (chosenX <= 200) {
            if (!up) {//make it bigger
                makeCircleBigger(canvas, "left");

            } else {//make it smaller
                makeCircleSmaller(canvas, "left");
            }
        } else if (chosenX >= getWidth() - 200) {
            if (!up) {//make it bigger
                makeCircleBigger(canvas, "right");

            } else {//make it smaller
                makeCircleSmaller(canvas, "right");
            }
        } else {
            if (!up) {//make it bigger
                makeCircleBigger(canvas, "");

            } else {//make it smaller
                makeCircleSmaller(canvas, "");
            }

        }
    }

    /**
     * @param canvas to draw border
     * @param borderChange
     * @param s shows the location which can be right or left or non
     */
    private void drawCircleBorder(Canvas canvas, float borderChange, String s) {

        final Path path = new Path();

        float startX = chosenX - 100;
        float startY = (float) (getHeight() / 2);

        borderChange = setInterpolator(borderChange);
        if (s.equals("left")) {// draw left part
            path.moveTo(margin, startY - (chosenX - margin - 50) * 20 / 100);
            path.cubicTo(startX + 50, startY - (chosenX - margin - 50) * 20 / 100,
                    startX + 20, (float) (startY - (chosenX - margin - 50) * 20 / 100 + 20 - borderChange * 1.5 - 60),
                    startX + 100, (float) (startY - borderChange * 1.5 - 60));
            path.lineTo(startX + 100, startY - 20);
            canvas.drawPath(path, paint);
            if (chosenX == (margin + 53)) {
                final RectF arcBounds = new RectF(margin - 8, (float) (startY - borderChange * 1.5 - 60),
                        chosenX + 70, (float) (startY + borderChange * 1.5 + 60));
                // Draw the arc
                canvas.drawArc(arcBounds, 180F, 180F, true, paint);
            }
        } else {
            path.moveTo(startX - 50, startY - 20);
            path.cubicTo(startX + 50, startY - 20,
                    startX + 20, (float) (startY - borderChange * 1.5 - 60),
                    startX + 100, (float) (startY - borderChange * 1.5 - 60));
            path.lineTo(startX + 100, startY - 20);
            canvas.drawPath(path, paint);
        }

        if (s.equals("right")) { // draw right part
            path.moveTo(startX + 100, (float) (startY - borderChange * 1.5 - 60));
            path.cubicTo(startX + 180, (float) ((startY - (getWidth() - chosenX - 100) * 20 / 100) + 20 - borderChange * 1.5 - 60),
                    startX + 150, startY - (getWidth() - chosenX - 100) * 20 / 100,
                    getWidth() - margin, startY - (getWidth() - chosenX - 100) * 20 / 100);
            path.lineTo(startX + 100, startY - 20);
            canvas.drawPath(path, paint);
            if (chosenX == getWidth() - margin - 53) {
                final RectF arcBounds = new RectF(chosenX - 70, (float) (startY - borderChange * 1.5 - 60),
                        getWidth() - margin + 8, (float) (startY + borderChange * 1.5 + 60));
                // Draw the arc
                canvas.drawArc(arcBounds, 180F, 180f, true, paint);
            }
        } else {
            path.moveTo(startX + 100, (float) (startY - borderChange * 1.5 - 60));
            path.cubicTo(startX + 180, (float) (startY - borderChange * 1.5 - 60),
                    startX + 150, startY - 20,
                    startX + 200 + 50, startY - 20);
            path.lineTo(startX + 100, startY - 20);
            canvas.drawPath(path, paint);
        }

    }


    /**
     * draw bubbles
     * @param canvas used to draw circles
     */
    private void drawBubble(Canvas canvas) {
        for (Bubble bubble : bubbles) {
            paint2.setAlpha(bubble.getAlpha());
            canvas.drawCircle(bubble.getX(), (getHeight() >> 1) - bubble.getY(), bubble.getR(), paint2);
        }
    }

    /**
     * update alpha and r of bubbles
     */
    private void updateBubbles() {
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble bubble = it.next();
            bubble.setR(15 - (int) ((float) (System.currentTimeMillis() - bubble.getCreatedTime()) / bubblesDuration * 15));
            bubble.setAlpha(255 - (int) ((float) (System.currentTimeMillis() - bubble.getCreatedTime()) / bubblesDuration * 255));
            if (bubble.getAlpha() < 1) {
                it.remove();
            }
            invalidate();
        }

    }

    /**
     * add bubbles
     * @param r radius of bubbles
     */
    private void addBubbles(int x, int y, int r) {
        Bubble bubble = new Bubble(x, y, r, 255, System.currentTimeMillis());
        bubbles.add(bubble);
    }


    /**
     * make circle smaller
     * @param canvas
     * @param s
     */
    private void makeCircleSmaller(Canvas canvas, String s) {

        goDown = setInterpolator(goDown);
        drawCircleBorder(canvas, borderGoDown, s);
        paint.setColor(circleColor);
        canvas.drawCircle(chosenX, (getHeight() >> 1) - 15 - goDown, 30 + goDown / 3, paint);

        //draw txt
        txtPaint.setTextSize(40 + goDown / 3);
        canvas.drawText(setText(chosenX), chosenX, (getHeight() >> 1) - goDown, txtPaint);

        if (goDown < 5)
            goDown = 0;

    }

    /**
     * make circle bigger and rise up when user touch
     * @param canvas
     * @param s is state of seek bar
     */
    private void makeCircleBigger(Canvas canvas, String s) {
        goUp = setInterpolator(goUp);
        drawCircleBorder(canvas, borderGoUp, s);
        paint.setColor(circleColor);
        canvas.drawCircle(chosenX, (getHeight() >> 1) - 15 - goUp, 30 + goUp / 3, paint);

        //draw txt
        txtPaint.setTextSize(30 + goUp / 2);
        canvas.drawText(setText(chosenX), chosenX, (getHeight() >> 1) - goUp, txtPaint);
    }


    /**
     * use overshoot function for animate seek bar
     * */
    private float setInterpolator(float x) {
        x = (float) ((float) ((float) Math.pow(2, (-10 * x / 100)) * Math.sin(2 * 3.1415926535
                * (x / 100 - (0.3 / 4)))) + 0.5) * 100;
        return x;
    }


    /**
     * @return the x in chosen range
     */
    @SuppressLint("DefaultLocale")
    private String setText(Float chosenX) {

        int x = (int) (startRange + (endRange - startRange) * (chosenX - (margin + 53)) /
                (getWidth() - 2 * (margin + 53)));
        if (x > endRange)
            x = endRange;
        if (x < startRange)
            x = startRange;

        if (seekBarLocation != null)
            seekBarLocation.setX(x);

        return String.valueOf(x);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startAnimation();
            chosenX = event.getX();
            if (chosenX < margin + 53)
                chosenX = margin + 53;
            else if (chosenX > getWidth() - margin - 53)
                chosenX = getWidth() - margin - 53;
            up = false;
            return true;
        } else if ((event.getAction() == MotionEvent.ACTION_MOVE)) {
            chosenX = event.getX();
            if (chosenX < margin + 53)
                chosenX = margin + 53;
            else if (chosenX > getWidth() - margin - 53)
                chosenX = getWidth() - margin - 53;
            up = false;
            this.invalidate();
            addBubbles((int) chosenX, random.nextInt(100), random.nextInt(10));
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            startAnimation();
            up = true;
            this.invalidate();
            return false;
        } else {
            return false;
        }
    }

    /**
     * set the range of the seek bar
     */
    public void setRange(int startRange, int endRange) {
        this.startRange = startRange;
        this.endRange = endRange;
    }


    /**
     * set colors for seek bar
     */
    public void setColor(String circleColor, String mainColor, String fontColor) {
        this.mainColor = Color.parseColor(mainColor);
        this.circleColor = Color.parseColor(circleColor);
        this.signColor = Color.parseColor(fontColor);
        invalidate();
    }


    /**
     * set animations for seek bar
     */
    public void startAnimation() {

        //for circle
        ValueAnimator animator = ValueAnimator.ofFloat(0, 60);
        animator.setDuration(signDuration);

        animator.addUpdateListener(valueAnimator -> {
            goUp = ((Float) valueAnimator.getAnimatedValue()).intValue();
            goDown = 60 - goUp;
            invalidate();
        });
        animator.start();

        ValueAnimator animator2 = ValueAnimator.ofFloat(0, 60);
        animator2.setDuration(signDuration);

        animator2.addUpdateListener(valueAnimator -> {
            borderGoUp = ((Float) valueAnimator.getAnimatedValue()).intValue();
            borderGoDown = 60 - borderGoUp;
            invalidate();
        });
        animator2.start();
    }


    /**
     * @param x is sign first location in seek bar
     */
    public void setSignFirstLocation(int x) {
        signFirstLocation = x;
    }

    /**
     *
     * @param typeface is the font of number
     */
    public void setFontForNum(Typeface typeface) {
        txtPaint.setTypeface(typeface);
        invalidate();
    }

    /**
     * @param seekBarLocation first location of seek bar
     */
    public void getSeekBarLocation(SeekBarLocation seekBarLocation) {
        this.seekBarLocation = seekBarLocation;
    }

    /**
     * @param signDuration duration that sign arise
     */
    public void setSignDuration(long signDuration) {
        this.signDuration = signDuration;
    }

    /**
     *
     * @param bubblesDuration duration that bubbles remain
     */
    public void setBubblesDuration(long bubblesDuration) {
        this.bubblesDuration = bubblesDuration;
    }
}
