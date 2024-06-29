package com.jollitycn.mobilekeybroad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.jasonhong.core.common.Callback;

import java.util.ArrayList;
import java.util.List;

public class TouchTrackView extends View {
    public void setCallback(Callback<MotionPoint> callback) {
        this.callback = callback;
    }

    private Callback<MotionPoint> callback;
    private Paint paint;
    private Path path;
    private List<Point> points = new ArrayList<>();
  
    public TouchTrackView(Context context) {
        super(context);  
        init();  
    }  
  
    public TouchTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);  
        init();  
    }  
  
    public TouchTrackView(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        init();  
    }  
  
    private void init() {  
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);  
        paint.setStrokeWidth(5);  
        path = new Path();  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        canvas.drawPath(path, paint);  
  
        // 也可以绘制点，用于调试  
        for (Point point : points) {  
            canvas.drawCircle(point.x, point.y, 10, paint);  
        }  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();  
        float y = event.getY();  
  
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                path.moveTo(x, y);
                Point point = new Point((int) x, (int) y);
                points.add(point);
                if (callback != null) {
                    callback.onAction(new MotionPoint(event, point));
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                path.lineTo(x, y);
                Point point = new Point((int) x, (int) y);
                points.add(point);
                if (callback != null) {
                    callback.onAction(new MotionPoint(event, point));
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 可以选择在这里重置path或保留轨迹
                if (callback != null) {
                    callback.onAction(new MotionPoint(event, null));
                }
                path.reset();
                points.clear();
                break;
        }
  
        invalidate(); // 请求重绘  
        return true;  
    }  
}