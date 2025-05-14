package com.example.cctv2.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AreaSelectorView extends View {
    private Paint paint;
    private RectF rect;
    private float x1, y1, x2, y2;
    private boolean drawing = false;

    public AreaSelectorView(Context context) {
        super(context);
        init();
    }

    public AreaSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 0, 0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        rect = new RectF();

        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 최초 그리기 이후 x1 x2 y1 y2 값이 생겼을 경우만 draw
        if (drawing) {
            rect.set(Math.min(x1, x2), Math.min(y1, y2),
                    Math.max(x1, x2), Math.max(y1, y2));
            canvas.drawRect(rect, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 최초 클릭은 x1 = x2, y1 = y2로 모두 설정 후 해당 view 크기 만큼 클램핑
                x1 = Math.max(0, Math.min(event.getX(), getWidth()));
                y1 = Math.max(0, Math.min(event.getY(), getHeight()));
                x2 = Math.max(0, Math.min(event.getX(), getWidth()));
                y2 = Math.max(0, Math.min(event.getY(), getHeight()));
                drawing = true;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // 이후 움직임은 x2 y2에 대입후 view 크기 만큼 클램핑
                x2 = Math.max(0, Math.min(event.getX(), getWidth()));
                y2 = Math.max(0, Math.min(event.getY(), getHeight()));
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public RectF getSelectedRect() {
        float left = Math.min(x1, x2);
        float right = Math.max(x1, x2);
        float top = Math.min(y1, y2);
        float bottom = Math.max(y1, y2);
        return new RectF(left, top, right, bottom);
    }
}

