package com.mg.axe.xfermodesimple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Zaifeng on 2017/12/26.
 * 使用xfermode中的src_in模式叠加做头像。
 */

public class CustomHeadView extends View {

    private Bitmap DBitmap;

    private Bitmap SBitmap;

    private Paint mPaint;

    private PorterDuffXfermode xfermode;

    public CustomHeadView(Context context) {
        this(context, null);
    }

    public CustomHeadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    public void drawHead(int src, int dst) {
        SBitmap = BitmapFactory.decodeResource(getResources(), src, null);
        DBitmap = BitmapFactory.decodeResource(getResources(), dst, null);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(SBitmap.getWidth(),SBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(SBitmap, 0, 0, mPaint);
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(DBitmap, 0, 0, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }
}
