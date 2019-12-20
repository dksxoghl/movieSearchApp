package com.example.startproject2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;

public class PaintBoard extends View {
    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;
    float lastX, lastY;
    Path mPath = new Path();
    static final float
            TOUCH_TOLERANCE = 8;

    public PaintBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        System.out.println("페인트보드에 들어옴?~~~~~~~~~~~~~~~~~~~~~~`");
//        File signatureFile = new File(context.getExternalFilesDir(null),
//                "signature.png");
//        mBitmap = BitmapFactory.decodeFile(signatureFile.getAbsolutePath());
//        changeBitmap(mBitmap);
//        invalidate();
    }
    public PaintBoard(Context context) {
        super(context);
        init();
        System.out.println("페인트보드 생성자없는거~~~~~~~~~~~~~~~~");
    }
    public void changeBitmap(Bitmap bitmap) {
        System.out.println(bitmap+"왜안돼~~~~~~~~~~~~~~```");
        Canvas canvas = new Canvas();
        Bitmap copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas.setBitmap(copyBitmap);
        mBitmap = copyBitmap;
        mCanvas = canvas;
        System.out.println("인벨리드전줄~~~~~~~~~~~~~~~~~~~페인트보드");
        invalidate();
//        requestLayout();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3.0F);
        this.lastX = -1;
        this.lastY = -1;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(mBitmap==null) {
            System.out.println("온사이즈체인지드~~~~~~~~~~~~~~~~~~~~~~~");
            Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(img);
            canvas.drawColor(Color.WHITE);
            mBitmap = img;
            mCanvas = canvas;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            System.out.println("온드로우~~~~~~~~~~~~~~~~~~~페인트보드");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                mPath.rewind();
            case MotionEvent.ACTION_MOVE:
                processMove(event);
                break;
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
        }
        System.out.println("인벨리드전줄~~~~~~~~~~~~~~~~~~~페인트보드");
        invalidate();
        return true;
    }

    private void processMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            float cX = (x + lastX) / 2;
            float cY = (y + lastY) / 2;
            mPath.quadTo(lastX, lastY, cX, cY);
            lastX = x;
            lastY = y;
            mCanvas.drawPath(mPath, mPaint);
        }
    }

    private void touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        lastX = x;
        lastY = y;
        mPath.moveTo(x, y);
        mCanvas.drawPath(mPath, mPaint);
    }

}
