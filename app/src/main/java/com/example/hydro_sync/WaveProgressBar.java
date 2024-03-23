package com.example.hydro_sync;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaveProgressBar extends View {
    private Paint wavePaint;
    private int waveColor;
    private int progress;
    private ValueAnimator animator;
    private Paint textPaint;
    private float wavePhase; // Phase of the wave animation
    private int waveLength; // Length of one wave

    // Additional attributes for progress text size and font
    private float progressTextSize;
    private Typeface progressTextFont;

    public WaveProgressBar(Context context) {
        super(context);
        init();
    }

    public WaveProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        // Obtain attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WaveProgressBar,
                0, 0);

        try {
            waveColor = a.getColor(R.styleable.WaveProgressBar_waveColor, Color.BLUE);
            setWaveColor(waveColor);

            // Retrieve progress text attributes
            progressTextSize = a.getDimensionPixelSize(R.styleable.WaveProgressBar_progressTextSize, 40);
            setTextAttributes(); // Set text paint attributes
        } finally {
            a.recycle();
        }
    }

    // Add a method to allow setting the wave color programmatically
    public void setWaveColor(int color) {
        waveColor = color;
        wavePaint.setColor(waveColor);
        invalidate();
    }

    private void init() {
        // Initialize wave and progress bar colors
        waveColor = Color.BLUE;

        wavePaint = new Paint();
        wavePaint.setStyle(Paint.Style.FILL);

        // Initialize text paint
        textPaint = new Paint();
        setTextAttributes();

        wavePhase = 0;
        waveLength = 400; // Length of one wave

        animator = ValueAnimator.ofInt(0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (int) valueAnimator.getAnimatedValue();
                // Update wavePhase based on progress
                wavePhase = valueAnimator.getAnimatedFraction() * waveLength;
                invalidate(); // Redraw the view
            }
        });
    }

    // Set text paint attributes
    private void setTextAttributes() {
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(progressTextSize); // Text size
        textPaint.setTextAlign(Paint.Align.CENTER); // Center-align the text
        if (progressTextFont != null) {
            textPaint.setTypeface(progressTextFont); // Set custom font
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float waveHeight = height * progress / 100f;
        canvas.drawRect(0, height - waveHeight, width, height, wavePaint);

        drawWave(canvas, width, height - waveHeight, height);

        String text = progress + "%";
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        float x = width / 2f;
        float y = height / 2f + bounds.height() / 2f;
        canvas.drawText(text, x, y, textPaint);
    }

    private void drawWave(Canvas canvas, int width, float baseY, int height) {
        Path path = new Path();
        path.moveTo(0, baseY);
        for (int x = 0; x < width; x += 10) {
            float y = (float) (20 * Math.sin(2 * Math.PI * (x - wavePhase) / waveLength)) + baseY;
            path.lineTo(x, y);
        }
        path.lineTo(width, baseY);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();
        canvas.drawPath(path, wavePaint);
    }

    // Method to set progress directly based on level
    public void setLevelProgress(int level) {
        if (level >= 0 && level <= 100) {
            progress = level;
            wavePhase = (float) level / 100 * waveLength; // Adjust wavePhase based on level
            invalidate(); // Redraw the view
        }
    }

    // Method to start the animation
    public void startAnimation() {
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(5000);
        animator.start();
    }

    // Method to stop the animation
    public void stopAnimation() {
        animator.cancel();
    }
}

