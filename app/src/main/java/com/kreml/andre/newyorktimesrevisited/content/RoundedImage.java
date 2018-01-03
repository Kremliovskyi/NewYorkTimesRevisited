package com.kreml.andre.newyorktimesrevisited.content;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * A Drawable that draws an oval with given {@link Bitmap}
 */
public class RoundedImage extends Drawable {

    private final Paint mPaint;
    private final RectF mRectF;
    private final int mBitmapWidth;
    private final int mBitmapHeight;

    public RoundedImage(Bitmap bitmap) {
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        final BitmapShader shader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(mRectF, mPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRectF.set(bounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }

    /**
     * Method to crop the square from rectangle
     * @param bitmap
     * @param width width of the ImageView
     * @param height height of the ImageView
     * @return
     */
    public static RoundedImage getCroppedRoundedImageFromBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        Bitmap croppedBitmap;
        if (bitmap.getWidth() == bitmap.getHeight()) {
            croppedBitmap = bitmap;
        } else {
            if (bitmap.getWidth() >= bitmap.getHeight()) {
                croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2,
                        0,
                        bitmap.getHeight(),
                        bitmap.getHeight()
                );
            } else {
                croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                        bitmap.getWidth(),
                        bitmap.getWidth()
                );
            }
        }
        return new RoundedImage(Bitmap.createScaledBitmap(croppedBitmap, width, height, false));
    }
}
