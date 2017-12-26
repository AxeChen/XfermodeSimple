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
 * 使用xfermode中的src_in模式叠加做头像。和bitmapshader做头像一样，可以做出各种各样的头像
 */

public class CustomAvaterView extends View {

    private Bitmap DstBitmap;

    private Bitmap SRCBitmap;

    private Paint mPaint;

    public CustomAvaterView(Context context) {
        this(context, null);
    }

    public CustomAvaterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAvaterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        DstBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shade,null);
        SRCBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.xyjy6,null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(DstBitmap,0,0,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(SRCBitmap,0,0,mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }
}
