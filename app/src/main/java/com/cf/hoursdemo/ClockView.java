package com.cf.hoursdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class ClockView extends View {

    //时针角度
    private float mHourDegree;
    //分钟角度
    private float mMinuteDegree;
    //秒针角度
    private float mSencondDegree;
    //毫秒角度
    private float mMilliSecondDegree;
    //亮色，用于分针，秒针，渐变终止色
    private int mLightColor;
    //秒针画笔
    private Paint mSecondHandPaint;
    //加一个默认的Padding值，为了防止camera旋转时针时四周超出view的大小
    private float mDefaultPadding;

    private float mPaddingTop;
    //时针半径，不包括padding值
    private float mRadius;

    //秒针路径
    private Path mSecondHandPath = new Path();

    //分针路径
    private Path mMinuteHandPath = new Path();
    //分针画笔
    private Paint mMinuteHandPaint;
    //小事圆圈的外接矩形
    private RectF mCircleRectF = new RectF();

    //时针路径
    private Path mHourHandPath = new Path();
    //时针画笔
    private Paint mHourHandPaint;

    //暗度，圆弧，刻度线，时针，渐变起色
    private int mDarkColor;

    public ClockView(Context context) {
        this(context, null, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //从xml中读取属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0);
        mLightColor = typedArray.getColor(R.styleable.ClockView_clock_lightColor, Color.parseColor("#ffffff"));
        typedArray.recycle();

        //初始化 画笔
        mSecondHandPaint = new Paint();
        mSecondHandPaint.setStyle(Paint.Style.FILL);
        mSecondHandPaint.setColor(mLightColor);

        mMinuteHandPaint = new Paint();

        mHourHandPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom()) / 2;//各指针长度
        mDefaultPadding = 0.12f * mRadius;
        mPaddingTop = mDefaultPadding + h / 2 - mRadius + getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getCurrentTime();
        drawSecondNeedle(canvas);
        drawMinuteNeedle(canvas);
        drawHourHand(canvas);
        invalidate();
    }

    /**
     * @作者：陈飞
     * @说明：绘制时针
     * @创建日期: 2019/10/9 16:28
     */
    private void drawHourHand(Canvas mCanvas) {
        mCanvas.save();
        mCanvas.rotate(mHourDegree, getWidth() / 2, getHeight() / 2);

        mHourHandPath.reset();
        float offset = mPaddingTop;
        mHourHandPath.moveTo(getWidth() / 2 - 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 - 0.009f * mRadius, offset + 0.48f * mRadius);
        mHourHandPath.quadTo(getWidth() / 2, offset + 0.46f * mRadius, getWidth() / 2, offset + 0.48f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 + 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mHourHandPath.close();
        mHourHandPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(mHourHandPath, mHourHandPaint);

        mCircleRectF.set(getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius, getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius);
        mHourHandPaint.setStyle(Paint.Style.STROKE);
        mHourHandPaint.setStrokeWidth(0.01f * mRadius);
        mCanvas.drawArc(mCircleRectF, 0, 360, false, mHourHandPaint);
        mCanvas.restore();
    }

    /**
     * @作者：陈飞
     * @说明：绘制分针
     * @创建日期: 2019/10/9 16:04
     */
    private void drawMinuteNeedle(Canvas mCanvas) {
        mCanvas.save();//save用来保存Canvas的状态，save之后可以调用Canvas的平移，缩放，旋转，错切，裁剪的操作
        mCanvas.rotate(mMinuteDegree, getWidth() / 2, getHeight() / 2);
        //恢复路径
        mSecondHandPath.reset();

        float offset = mPaddingTop;
        mMinuteHandPath.moveTo(getWidth() / 2 - 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mMinuteHandPath.lineTo(getWidth() / 2 - 0.008f * mRadius, offset + 0.365f * mRadius);
        mMinuteHandPath.quadTo(getWidth() / 2, offset + 0.345f * mRadius, getWidth() / 2 + 0.0008f * mRadius, offset + 0.365f * mRadius);
        mMinuteHandPath.lineTo(getWidth() / 2 + 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius);
        mMinuteHandPath.close();
        mMinuteHandPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(mMinuteHandPath, mMinuteHandPaint);

        mCircleRectF.set(getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius, getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius);
        mMinuteHandPaint.setStyle(Paint.Style.STROKE);
        mMinuteHandPaint.setStrokeWidth(0.02f * mRadius);
        mCanvas.drawArc(mCircleRectF, 0, 360, false, mMinuteHandPaint);

        mCanvas.restore();
    }

    /**
     * @作者：陈飞
     * @说明：绘制秒针
     * @创建日期: 2019/10/9 16:03
     */
    private void drawSecondNeedle(Canvas mCanvas) {
        mCanvas.save();//save用来保存Canvas的状态，save之后可以调用Canvas的平移，缩放，旋转，错切，裁剪的操作
        //旋转
        mCanvas.rotate(mSencondDegree, getWidth() / 2, getHeight() / 2);
        mSecondHandPaint.reset();


        float offset = mPaddingTop;
        mSecondHandPath.moveTo(getWidth() / 2, offset + 0.26f * mRadius);//绘制三角尖
        mSecondHandPath.lineTo(getWidth() / 2 - 0.05f * mRadius, offset + 0.34f * mRadius);
        mSecondHandPath.lineTo(getWidth() / 2 + 0.05f * mRadius, offset + 0.34f * mRadius);
        mSecondHandPath.close();

        mSecondHandPaint.setColor(mLightColor);
        mCanvas.drawPath(mSecondHandPath, mSecondHandPaint);


        mCanvas.restore();//用来恢复保存之前的状态，防止save后对canvas操作后续有影响
    }

    /**
     * 获取当前时间
     */
    private void getCurrentTime() {
        Calendar instance = Calendar.getInstance();
        float milliSecond = instance.get(Calendar.MILLISECOND);
        float second = instance.get(Calendar.SECOND) + milliSecond / 1000; //精确到小数点后，保证圆滑
        float minute = instance.get(Calendar.MINUTE) + second / 60;
        float hour = instance.get(Calendar.HOUR) + minute / 60;
        mMilliSecondDegree = milliSecond / 20 * 360;
        mSencondDegree = second / 60 * 360;
        mMinuteDegree = minute / 60 * 360;
        mHourDegree = hour / 60 * 360;
    }

    /**
     * @作者：陈飞
     * @说明：解析高度
     * @创建日期: 2019/10/9 13:27
     */
    private int measureDimension(int measureSpec) {
        int defaultSize = 800;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY://对高度没有限制，可以设置任何尺寸
                return size;
            case MeasureSpec.AT_MOST://当前尺寸是View的最大尺寸
                return Math.min(size, defaultSize);
            case MeasureSpec.UNSPECIFIED:
                return defaultSize;
            default:
                return defaultSize;
        }
    }
}
