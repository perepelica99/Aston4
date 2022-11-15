package com.example.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends View {

    private final String TAG = getClass().getSimpleName();

    private Paint mCirclePaint,mPointerPaint,mNumPaint;

    private float mClockRingWidth,mClockRadius,mDefaultWidth,mDefaultLength,
            mSpecialWidth,mSpecialLength,mHWidth,mMWidth,mSWidth;


    private int mCircleColor,mHColor,mMColor,mSColor,mNumColor;

    private int mWidth, mHeight, mCenterX, mCenterY;

    private float mH,mM,mS;

    private Timer mTimer=new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (mS == 360) {
                mS = 0;
            }
            if (mM == 360){
                mM = 0;
            }
            if (mH == 360){
                mH = 0;
            }
            mS = mS + 6;
            mM = mM + 0.1f;
            mH = mH + 1.0f/120;
            postInvalidate();
        }
    };

    public void start() {
        mTimer.schedule(task,0,1000);
    }

    public void setTime(int h, int m, int s) {
        if (h >= 24 || h < 0 || m >= 60 || m < 0 || s >= 60 || s < 0) {
            Toast.makeText(getContext(), "неправильное время", Toast.LENGTH_SHORT).show();
            return;
        }

        if (h >= 12) {
            mH = (h + m * 1.0f/60f + s * 1.0f/3600f - 12)*30f-180;
        } else {
            mH = (h + m * 1.0f/60f + s * 1.0f/3600f)*30f-180;
        }
        mM = (m + s * 1.0f/60f) *6f-180;
        mS = s * 6f-180;
    }


    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
        initPaint();
        Calendar mCalendar= Calendar.getInstance();

        int hours = mCalendar.get(Calendar.HOUR);

        int minutes = mCalendar.get(Calendar.MINUTE);

        int seconds=mCalendar.get(Calendar.SECOND);
        setTime(hours,minutes,seconds);

        start();
    }


    private void init(Context context,AttributeSet attributeSet){
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.Clock);
        mClockRingWidth=ta.getDimension(R.styleable.Clock_mClockRingWidth,SizeUtils.dp2px(context,4));
        mDefaultWidth=ta.getDimension(R.styleable.Clock_mDefaultWidth,SizeUtils.dp2px(context,1));
        mDefaultLength=ta.getDimension(R.styleable.Clock_mDefaultLength,SizeUtils.dp2px(context,8));
        mSpecialWidth=ta.getDimension(R.styleable.Clock_mSpecialWidth,SizeUtils.dp2px(context,2));
        mSpecialLength=ta.getDimension(R.styleable.Clock_mSpecialLength,SizeUtils.dp2px(context,14));
        mHWidth=ta.getDimension(R.styleable.Clock_mHWidth,SizeUtils.dp2px(context,5));
        mMWidth=ta.getDimension(R.styleable.Clock_mMWidth,SizeUtils.dp2px(context,10));
        mSWidth=ta.getDimension(R.styleable.Clock_mSWidth,SizeUtils.dp2px(context,10));
        mCircleColor=ta.getColor(R.styleable.Clock_mCircleColor, Color.BLACK);
        mHColor=ta.getColor(R.styleable.Clock_mHColor, Color.BLUE);
        mMColor=ta.getColor(R.styleable.Clock_mMColor, Color.RED);
        mSColor=ta.getColor(R.styleable.Clock_mSColor, Color.BLACK);
        mNumColor=ta.getColor(R.styleable.Clock_mNumColor, Color.BLACK);

        ta.recycle();

    }


    private void initPaint() {

        mCirclePaint=new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mPointerPaint=new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //  mPointerPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureSize(true, widthMeasureSpec);
        int height = getMeasureSize(false, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        mHeight=h;
        mCenterX=w/2;
        mCenterY=h/2;
        mClockRadius= (float) ((float) (w/2)*0.8);
    }


    private int getMeasureSize(boolean isWidth, int measureSpec) {

        int result = 0;

        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                if (isWidth) {
                    result = getSuggestedMinimumWidth();
                } else {
                    result = getSuggestedMinimumHeight();
                }
                break;
            case MeasureSpec.AT_MOST:
                if (isWidth)
                    result = Math.min(specSize, mWidth);
                else
                    result = Math.min(specSize, mHeight);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mCenterX,mCenterY);

        drawCircle(canvas);

        drawPointer(canvas);
    }


    private void drawCircle(Canvas canvas) {
        mCirclePaint.setStrokeWidth(mClockRingWidth);
        mCirclePaint.setColor(mCircleColor);

        canvas.drawCircle(0,0,mClockRadius,mCirclePaint);
        for (int i = 0; i < 60; i++) {
            if (i%5==0){
                mCirclePaint.setStrokeWidth(mSpecialWidth);
                mCirclePaint.setColor(mHColor);
                canvas.drawLine(0,-mClockRadius+mClockRingWidth/2,0,-mClockRadius+mSpecialLength,mCirclePaint);
            }else {
                mCirclePaint.setStrokeWidth(mDefaultWidth);
                mCirclePaint.setColor(mHColor);
                canvas.drawLine(0,-mClockRadius+mClockRingWidth/2,0,-mClockRadius+mDefaultLength,mCirclePaint);
            }
//
            canvas.rotate(6);
        }
    }




    private void drawPointer(Canvas canvas) {
        // часовая стрелка
        canvas.save();
        mPointerPaint.setColor(mHColor);
        mPointerPaint.setStrokeWidth(mHWidth);
        canvas.rotate(mH, 0, 0);
        canvas.drawLine(0, -70, 0,
                (float) (mClockRadius*0.65), mPointerPaint);
        canvas.restore();

        //минутная стрелка
        canvas.save();
        mPointerPaint.setColor(mMColor);
        mPointerPaint.setStrokeWidth(mMWidth);
        canvas.rotate(mM, 0, 0);
        canvas.drawLine(0, -70, 0,
                (float) (mClockRadius*0.5), mPointerPaint);
        canvas.restore();

        //секундная стрелка
        canvas.save();
        mPointerPaint.setColor(mSColor);
        mPointerPaint.setStrokeWidth(mSWidth);
        canvas.rotate(mS, 0, 0);
        canvas.drawLine(0, -70, 0,
                (float) (mClockRadius*0.35), mPointerPaint);
        canvas.restore();

        //точка в центре
        mPointerPaint.setColor(mSColor);
        canvas.drawCircle(0,0,mHWidth/1000,mPointerPaint);

    }





    private static class SizeUtils {
        static int dp2px(Context context, float dp) {
            final float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5);
        }

        static int px2dp(Context context, float px) {
            final float density = context.getResources().getDisplayMetrics().density;
            return (int) (px / density + 0.5);
        }
    }

}
