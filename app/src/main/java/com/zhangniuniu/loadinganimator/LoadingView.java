package com.zhangniuniu.loadinganimator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author：zhangyong on 2017/7/25 13:17
 * Email：zhangyonglncn@gmail.com
 * Description：
 */

public class LoadingView extends View {

    private Paint mPaint;
    private Bitmap loadingBg;
    private Bitmap loadingNomal;

    private static final String TAG = "LoadingView";
    private int bitmapHeight;
    private int bitmapWidth;
    private float percent = 0;
    private ValueAnimator valueAnimator;
    private Matrix matrix;
    //完整图片的rect
    private Rect allBitmapRect;
    private Rect positonRect;
    private Rect clipRect;
    private Rect normalClipRect;

    public LoadingView(Context context) {
        super(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadingBg = BitmapFactory.decodeResource(getResources(), R.drawable.ll_icon_bg_loading);
        loadingNomal = BitmapFactory.decodeResource(getResources(), R.drawable.ll_icon_loading);
        bitmapHeight = loadingBg.getHeight();
        bitmapWidth = loadingBg.getWidth();
        mPaint = new Paint();
        matrix = new Matrix();

        positonRect = new Rect();
        clipRect = new Rect();
        normalClipRect = new Rect();
        allBitmapRect = new Rect(0, 0, loadingNomal.getWidth(), loadingNomal.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            sizeWidth = bitmapWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            sizeHeight = bitmapHeight;
        }

        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    //将canvas.setMatrix(matrix) 在此执行的所有draw的内容全部变大或变小
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取屏幕宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        //如果bitmap宽高大于view宽高，需要将canvas放大，将bitmap画在中间；如果bitmap宽高小于view宽高，将bitmap直接滑到canvas上
        int scaleType; //0是以高为准  1以宽为准
        if (bitmapWidth * 1.0f / bitmapHeight > viewWidth * 1.0f / viewHeight) {
            //如果图片宽高比大于View宽高比  以高为准进行缩放
            scaleType = 1;
        } else {
            scaleType = 0;
        }

        int left, top, right, bottom;
        float matrixScale;
        if (scaleType == 0) {
            matrixScale = bitmapHeight * 1.0f / viewHeight;
        } else {
            matrixScale = bitmapWidth * 1.0f / viewWidth;
        }
        canvas.save();
        //将画布等比例缩放只bitmap小的一边

        matrix.setScale(1.0f / matrixScale, 1.0f / matrixScale);
        canvas.setMatrix(matrix);

        top = (int) ((viewHeight * matrixScale - bitmapHeight) / 2);
        bottom = (int) (viewHeight * matrixScale - top);
        left = (int) ((viewWidth * matrixScale - bitmapWidth) / 2);
        right = (int) (viewWidth * matrixScale - left);


        positonRect.left = left;
        positonRect.top = top;
        positonRect.right = right;
        positonRect.bottom = bottom;

        canvas.drawBitmap(loadingBg, allBitmapRect, positonRect, mPaint);

        clipRect.left = left;
        clipRect.top = (int) (bottom - loadingNomal.getHeight() * percent);
        clipRect.right = right;
        clipRect.bottom = bottom;


        normalClipRect.left = 0;
        normalClipRect.top = (int) (loadingNomal.getHeight() * (1 - percent));
        normalClipRect.right = loadingNomal.getWidth();
        normalClipRect.bottom = loadingNomal.getHeight();
        /** 方式一 取出百分比需要展示的图片，设置在对应的画布上*/

        canvas.drawBitmap(loadingNomal, normalClipRect, clipRect, mPaint);

        /** 方式二 取出整体图片，裁剪需要展示的部分 由此可见裁剪只作用于设置clip之后的代码*/
//        canvas.clipRect(clipRect);
//        canvas.drawBitmap(loadingNomal, allBitmapRect, positonRect, mPaint);

        canvas.restore();
    }

    boolean isAnim = false;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void startAnimator() {
        isAnim = true;
        valueAnimator = ValueAnimator.ofFloat(0.1f, 1.0f);
        valueAnimator.setDuration(4000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void stopAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            isAnim = false;
        }
    }

    public boolean isAnim() {
        return isAnim;
    }
}
