package com.iamasoldier6.soldiernote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Iamasoldier6 on 6/1/16.
 */
public class NewNoteView extends View {

    float h = 0; //视图高
    float w = 0; //视图宽
    Paint p; //画笔

    Context context; //活动上下文

    public NewNoteView(Context context) {
        this(context, null);
    }

    public NewNoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (w == 0) {
            w = getWidth();
            h = getHeight();
        }

        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.rgb(0x80, 0x80, 0x80));

        //画分割线
        p.setStrokeWidth(1);
        canvas.drawLine(0, 0, w, 0, p);
    }

}

