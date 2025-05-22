package com.example.cctv2.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AreaView extends View {
    private Paint border, fill;
    private List<RectF> rects;

    private int highLightIndex = -1;

    public AreaView(Context context) {
        super(context);
        init();
    }

    public AreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        border = new Paint();
        border.setColor(Color.argb(255, 255, 0, 0));
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(4);
        fill = new Paint();
        fill.setColor(Color.argb(50, 255, 0, 0));
        fill.setStyle(Paint.Style.FILL);
        rects = new ArrayList<>();

        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!rects.isEmpty()) {
            for (int i = 0; i < rects.size(); i++) {
                RectF rect = rects.get(i);
                if (i == this.highLightIndex) {
                    fill.setColor(Color.argb(100, 0, 255, 0));
                    border.setColor(Color.argb(255, 0, 255, 0));
                }
                else {
                    fill.setColor(Color.argb(50, 255, 0, 0));
                    border.setColor(Color.argb(255, 255, 0, 0));
                }
                canvas.drawRect(rect, border);
                canvas.drawRect(rect, fill);
            }
        }
    }

    public void setHighLightIndex(int pos) {
        this.highLightIndex = pos;
        invalidate();
    }

    public void addRect(float x1, float y1, float x2, float y2) {
        float left = Math.min(x1, x2);
        float right = Math.max(x1, x2);
        float top = Math.min(y1, y2);
        float bottom = Math.max(y1, y2);
        RectF area = new RectF(left * getWidth(),
                top * getHeight(),
                right * getWidth(),
                bottom* getHeight());
        Log.i("AreaView", String.format("area추가 %.2f,%.2f,%.2f,%.2f", area.left, area.top, area.right, area.bottom));
        rects.add(area);
        invalidate();
    }

    public void resetRect() {
        rects.clear();
        invalidate();
    }
}

