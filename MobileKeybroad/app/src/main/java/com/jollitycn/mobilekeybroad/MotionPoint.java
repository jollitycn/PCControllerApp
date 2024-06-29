package com.jollitycn.mobilekeybroad;

import android.graphics.Point;
import android.view.MotionEvent;

public class MotionPoint {
    private MotionEvent event;
    private Point point;

    public MotionPoint(MotionEvent event, Point point) {
        this.event = event;
        this.point= point;
    }

    public MotionEvent getEvent() {
        return event;
    }

    public void setEvent(MotionEvent event) {
        this.event = event;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
