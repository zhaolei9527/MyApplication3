package com.example.administrator.myapplication3.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.myapplication3.pojo.drawxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */
public class MyView extends View {
    // 定义画笔
    private Paint mPaint;
    private float startx;
    private float starty;

    public List<drawxy> getList() {
        return list;
    }

    public void setList(List<drawxy> list) {
        this.list = list;
    }

    private List<drawxy> list = new ArrayList<>();

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化画笔、Rect
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        //设置画笔颜色
        mPaint.setColor(Color.RED);
        //设置画笔粗细
        mPaint.setStrokeWidth(10);
    }

    private float Startx;
    private float startY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (list.size() != 0) {
            drawxy drawxy = list.get(0);
            Startx = drawxy.getX();
            startY = drawxy.getY();
            canvas.drawCircle(Startx, startY, 5, mPaint);
            mPaint.setTextSize(40);
            canvas.drawText("起点",Startx,startY,mPaint);
        }
        for (int i = 0; i < list.size(); i++) {
            drawxy drawxy = list.get(i);
            if (Startx != drawxy.getX()) {
                canvas.drawCircle(Startx, startY, 5, mPaint);
                canvas.drawCircle(drawxy.getX(), drawxy.getY(), 5, mPaint);
            }
            //画线
            canvas.drawLine(Startx, startY, drawxy.getX(), drawxy.getY(), mPaint);
            Startx = drawxy.getX();
            startY = drawxy.getY();
            if (i == list.size() - 1) {
                //设置画笔颜色
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle(Startx, startY, 10, mPaint);
                mPaint.setColor(Color.RED);
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startx = event.getX();
                starty = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float vy = y - starty;
                float vx = x - startx;
                if (Math.abs(vx) > Math.abs(vy)) {
                    changeviewx(vx);
                } else {
                    changeviewy(vy);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void changeviewy(float v) {
        if (v > 0) {
            // Log.i("日志输出", "上边？" + v);
        } else {
            // Log.i("日志输出", "下边？" + v);
        }
        for (int i = 0; i < list.size(); i++) {
            drawxy drawxy = list.get(i);
            list.remove(drawxy);
            drawxy.setY((int) (drawxy.getY() + (v / 70)));
            list.add(i, drawxy);
        }
        invalidate();
    }

    public void changeviewx(float v) {
        if (v > 0) {
            // Log.i("日志输出", "左边？" + v);
        } else {
            // Log.i("日志输出", "右边？" + v);
        }
        for (int i = 0; i < list.size(); i++) {
            drawxy drawxy = list.get(i);
            list.remove(drawxy);
            drawxy.setX((int) (drawxy.getX() + (v / 70)));
            list.add(i, drawxy);
        }
        invalidate();
    }
}