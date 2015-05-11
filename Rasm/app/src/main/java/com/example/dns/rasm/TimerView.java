package com.example.dns.rasm;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimerView extends View {
    Resources r;

    private int min_count = 15;
    private String minStr = "15";
    private int sec_count = 47;
    private boolean action = true;

    private Paint textPaint;
    private Paint arcPaint;
    private Paint circlePaint;
    private float textHeight;
    private float textWidth;
    private float ringWidth = 14;
    private Rect  mTextBoundRect;



    public TimerView(Context context) {
        super(context);
        initTimerView();
    }
    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTimerView();
    }
    public TimerView(Context context, AttributeSet ats,	int defaultStyle) {
        super(context, ats, defaultStyle);
        initTimerView();
    }
    public void  setMinSec(int _min, int _sec, boolean _action){
        min_count = _min;
        sec_count = _sec;
        minStr = Integer.toString(min_count);
        action = _action;
    }

    protected void initTimerView() {
        setFocusable(true);
        // Get external resources
        r = this.getResources();

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(r.getColor(R.color.TV_arc_color));
        arcPaint.setStrokeWidth(15);
        arcPaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.TV_circle_color));
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.TV_text_color));
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Align.LEFT);

        mTextBoundRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;

        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 100;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getMeasuredHeight();
        int width =getMeasuredWidth();

        int px = width/2;
        int py = height/2;
        Point center = new Point(px, py);

        float radius = Math.min(px, py) - ringWidth;

        RectF boundingBox = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        RectF innerBoundingBox = new RectF(center.x - radius + ringWidth, center.y - radius + ringWidth, center.x + radius - ringWidth, center.y + radius - ringWidth);

        if (action)
            circlePaint.setColor(r.getColor(R.color.TV_circle_act_color));
        else
            circlePaint.setColor(r.getColor(R.color.TV_circle_color));
        canvas.drawCircle(center.x, center.y, radius, circlePaint);

        canvas.drawArc(boundingBox,-90, sec_count * 6, false, arcPaint);

        textPaint.setTextSize(height / 2.5f);
        textWidth = textPaint.measureText(minStr);
        textPaint.getTextBounds(minStr, 0, minStr.length(), mTextBoundRect);
        textHeight = mTextBoundRect.height();
        canvas.drawText(minStr, center.x - (textWidth / 2f), center.y + (textHeight / 2f), textPaint );

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {

        }
        return false;
    }
}
