package com.huan.percy.ghostview.viewLib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Percy on 2017/3/10.
 */

public class GhostView extends View {

    //The width and height of view
    private int mWidth, mHeight;
    //default width and height(WRAP_CONTENT)
    private int mDefaultWidth = 120;
    private int mDefaultHeight = 180;

    public GhostView(Context context) {
        super(context);
        initPaint();
    }

    public GhostView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public GhostView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY) {
            mWidth = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            mWidth = Math.min(mDefaultWidth, specSize);
        }
        return mWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY) {
            mHeight = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            mHeight = Math.min(mDefaultHeight, specSize);
        }
        return mHeight;
    }

    //init Paint:BodyPaint, EyesPaint, ShadowPaint
    private Paint mBodyPaint, mEyesPaint, mShadowPaint;
    private void initPaint() {
        mBodyPaint = new Paint();
        mBodyPaint.setAntiAlias(true);
        mBodyPaint.setStyle(Paint.Style.FILL);
        mBodyPaint.setColor(Color.WHITE);

        mEyesPaint = new Paint();
        mEyesPaint.setAntiAlias(true);
        mEyesPaint.setStyle(Paint.Style.FILL);
        mEyesPaint.setColor(Color.BLACK);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(Color.argb(60, 0, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHead(canvas);
        drawShadow(canvas);
        drawBody(canvas);
        drawEyes(canvas);
        startAnim();
    }

    //the radius of head
    private int mHeadRadius;
    //X of centre dot of head
    private int mHeadCentreX;
    //Y of centre dot of head
    private int mHeadCentreY;
    //X of left-most dot of head
    private int mHeadLeftX;
    //X of right-most dot of head
    private int mHeadRightX;
    //padding of top
    private int mPaddingTop = 20;
    private void drawHead(Canvas canvas) {
        mHeadRadius = mWidth / 3;
        mHeadCentreX = mWidth / 2;
        mHeadCentreY = mWidth / 3 + mPaddingTop;
        mHeadLeftX = mHeadCentreX - mHeadRadius;
        mHeadRightX = mHeadCentreX + mHeadRadius;
        canvas.drawCircle(mHeadCentreX, mHeadCentreY, mHeadRadius, mBodyPaint);
    }

    //the area of shadow
    private RectF mRectShadow;
    //the padding of body and shadow
    private int paddingShadow;
    private void drawShadow(Canvas canvas) {
        paddingShadow = mHeight / 10;
        mRectShadow = new RectF();
        mRectShadow.top = mHeight * 8 / 10;
        mRectShadow.bottom = mHeight * 9 / 10;
        mRectShadow.left = mWidth / 4;
        mRectShadow.right = mWidth * 3 / 4;
        canvas.drawArc(mRectShadow, 0, 360, false, mShadowPaint);

    }

    private Path mPath = new Path();
    //the width that body over head
    private int mGhostBodySpace;
    //the width and height of single pleat
    private int mSkirtWidth, mSkirtHeight;
    //the number of pleats
    private int mSkirtCount = 7;

    private void drawBody(Canvas canvas) {
        mGhostBodySpace = mHeadRadius * 2 / 15;
        mSkirtWidth = (mHeadRadius * 2 - mGhostBodySpace * 2) / mSkirtCount;
        mSkirtHeight = mHeight / 16;
        //the right part of body
        mPath.moveTo(mHeadLeftX, mHeadCentreY);
        mPath.lineTo(mHeadRightX, mHeadCentreY);
        mPath.quadTo(mHeadRightX + mGhostBodySpace, mRectShadow.top - paddingShadow,
                     mHeadRightX - mGhostBodySpace, mRectShadow.top - paddingShadow);

        // draw pleats from right ot left
        for (int i = 1; i <= mSkirtCount; i++) {
            if(i % 2 != 0) {
                mPath.quadTo(mHeadRightX - mGhostBodySpace- mSkirtWidth * i + (mSkirtWidth / 2), mRectShadow.top - paddingShadow - mSkirtHeight,
                        mHeadRightX - mGhostBodySpace - (mSkirtWidth * i), mRectShadow.top - paddingShadow);
            } else {
                mPath.quadTo(mHeadRightX - mGhostBodySpace- mSkirtWidth * i + (mSkirtWidth / 2), mRectShadow.top - paddingShadow + mSkirtHeight,
                        mHeadRightX - mGhostBodySpace - (mSkirtWidth * i), mRectShadow.top - paddingShadow);
            }
        }

        //change the left part path of body
        mPath.quadTo(mHeadLeftX - mGhostBodySpace, mRectShadow.top - paddingShadow, mHeadLeftX, mHeadCentreY);
        canvas.drawPath(mPath, mBodyPaint);
    }

    private void drawEyes(Canvas canvas) {
        //left eye
        canvas.drawCircle(mHeadCentreX, mHeadCentreY, mHeadRadius / 6, mEyesPaint);
        //right eye
        canvas.drawCircle(mHeadCentreX + mHeadRadius / 2, mHeadCentreY, mHeadRadius / 6, mEyesPaint);
    }

    private void startAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationX", 0, 1000);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setDuration(3000);
        animator.start();
    }
}
