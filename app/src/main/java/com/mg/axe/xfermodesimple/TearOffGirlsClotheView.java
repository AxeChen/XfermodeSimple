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
 * Created by Zaifeng on 2017/12/26.
 * 撕掉美女的衣服游戏，xfermode叠加模式SRC_OUT (其他的模式也可做，关键是哪个是s哪个是d)
 * 实际上这种效果还有一种就是那种刮刮卡的效果
 */

public class TearOffGirlsClotheView extends View implements View.OnTouchListener {

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

    public TearOffGirlsClotheView(Context context) {
        this(context, null);
    }

    public TearOffGirlsClotheView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TearOffGirlsClotheView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        SBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.a914);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.b914);
        DBitmap = Bitmap.createBitmap(SBitmap.getWidth(), SBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        bitCanvas = new Canvas(DBitmap);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    private Canvas bitCanvas;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);

        bitCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(DBitmap, 0, 0, mPaint);
        // 叠加模式
        mPaint.setXfermode(xfermode);
        canvas.drawBitmap(SBitmap, 0, 0, mPaint);
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
