package com.iamasoldier6.soldiernote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.iamasoldier6.soldiernote.activity.EditActivity;

/**
 * Created by Iamasoldier6 on 6/1/16.
 */
public class EditTopView extends View {

    float h = 0;
    float w = 0;
    float padding;
    Paint p;
    String text = null;

    Context context;

    public EditTopView(Context context) {
        this(context, null);
    }

    public EditTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (w == 0) {
            w = getWidth();
            h = getHeight();
            padding = h / 5;
        }
        // 画指向箭头
        p.setStrokeWidth(3);
        canvas.drawLine(padding, h / 2, padding * 2, h / 2 - padding, p);
        canvas.drawLine(padding, h / 2, padding * 2, h / 2 + padding, p);

        // 画文本
        p.setTextSize(h / 3);
        p.setStrokeWidth(1);
        if (null != text) {
            canvas.drawText(text, padding * 3, h / 2 + padding / 4 * 3, p);
        }
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x >= padding && x <= padding + 300 && y >= h / 2 - 20 && y <= h / 2 + 20) {
            EditActivity editActivity = (EditActivity) context;
            editActivity.insertToDb();
            editActivity.finish();
        }
        return super.onTouchEvent(event);
    }

}

