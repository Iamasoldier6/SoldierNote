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
public class SureCircleView extends View {

    /**
     * 视图风格：填充
     */
    public static final int FILL = 1;

    /**
     * 视图风格：非填充 default
     */
    public static final int STROKE = 2;

    float h = 0;
    float w = 0;
    float r;
    Paint p;

    boolean isCheck = false;

    public SureCircleView(Context context) {
        this(context, null);
    }

    public SureCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.rgb(101, 147, 74));
        p.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (w == 0) {
            w = getWidth();
            h = getHeight();
            r = h / 5;
        }
        if (!isCheck) {
            //未选择
            p.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(w - r - 2, h / 2, r, p);
        } else {
            //选择
            p.setStyle(Paint.Style.FILL);
            canvas.drawCircle(w - r - 2, h / 2, r, p);
        }
    }

    /**
     * 设置填充风格
     *
     * @param style
     */
    public void setStyle(int style) {
        switch (style) {
            case FILL:
                isCheck = true;
                break;
            case STROKE:
                isCheck = false;
                break;
            default:
                try {
                    throw new Exception("illegal argument !");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        invalidate();
    }

}

