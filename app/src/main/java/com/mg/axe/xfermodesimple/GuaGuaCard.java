package com.mg.axe.xfermodesimple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Zaifeng on 2018/1/14.
 */

public class GuaGuaCard extends View implements View.OnTouchListener {
    /**
     * 原图
     */
    private Bitmap SBitmap;

    /**
     * 目标图片
     */
    private Bitmap DBitmap;

    private Bitmap bitmap;

    private Paint mPaint;

    private PorterDuffXfermode xfermode;

    /**
     * 记录手指划过的路劲
     */
    private Path mPath = new Path();

    private float startX;
    private float startY;

    public GuaGuaCard(Context context) {
        this(context, null);
    }

    public GuaGuaCard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaGuaCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 去掉硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init(context);
        setOnTouchListener(this);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(45);

        SBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.one);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.two);
        DBitmap = Bitmap.createBitmap(SBitmap.getWidth(), SBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        bitCanvas = new Canvas(DBitmap);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    private Canvas bitCanvas;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        bitCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(SBitmap, 0, 0, mPaint);
        // 叠加模式
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(DBitmap, 0, 0, mPaint);
        // 记得重置xfermode模式
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    // 移动的时候记录移动的路线
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                mPath.moveTo(startX, startY);
                break;
            case MotionEvent.ACTION_MOVE:
                float endx = motionEvent.getX();
                float endY = motionEvent.getY();
                mPath.quadTo(startX, startY, endx, endY);
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        postInvalidate();
        return true;
    }
}
