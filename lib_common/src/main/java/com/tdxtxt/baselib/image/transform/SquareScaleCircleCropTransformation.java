package com.tdxtxt.baselib.image.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.security.MessageDigest;
import jp.wasabeef.glide.transformations.BitmapTransformation;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-10-09
 *     desc   : 先拉伸为正方形后，再裁剪为圆形
 * </pre>
 */
public class SquareScaleCircleCropTransformation extends BitmapTransformation {
    public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;
    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);
    private static final int VERSION = 1;
    private static final String ID =
            "jp.wasabeef.glide.transformations.SquareScaleCircleCropTransformation." + VERSION;

    private int size;

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        this.size = Math.max(outWidth, outHeight);
        return TransformationUtils.circleCrop(pool, scaleSquare(pool, toTransform, size, size), size, size);

    }

    @Override
    public String toString() {
        return "SquareScaleCircleCropTransformation(size=" + size + ")";
    }

    @Override
    public boolean equals(Object o) {
        //注释去掉后，重复加载图片会闪烁
        return o instanceof SquareScaleCircleCropTransformation /*&& ((SquareScaleCircleCropTransformation) o).size == size*/;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + size * 10;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + size).getBytes(CHARSET));
    }



    public Bitmap scaleSquare(
            @NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height) {
        if (inBitmap.getWidth() == width && inBitmap.getHeight() == height) {
            return inBitmap;
        }
        final float widthPercentage = width / (float) inBitmap.getWidth();
        final float heightPercentage = height / (float) inBitmap.getHeight();

        // Round here in case we've decoded exactly the image we want, but take the floor below to
        // avoid a line of garbage or blank pixels in images.
        int targetWidth = Math.round(widthPercentage * inBitmap.getWidth());
        int targetHeight = Math.round(heightPercentage * inBitmap.getHeight());

        if (inBitmap.getWidth() == targetWidth && inBitmap.getHeight() == targetHeight) {
            return inBitmap;
        }

        Bitmap.Config config = getNonNullConfig(inBitmap);
        Bitmap toReuse = pool.get(targetWidth, targetHeight, config);

        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(inBitmap, toReuse);

        Matrix matrix = new Matrix();
        matrix.setScale(widthPercentage, heightPercentage);
        applyMatrix(inBitmap, toReuse, matrix);

        return toReuse;
    }

    private Bitmap.Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    private void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap, Matrix matrix) {
        try {
            Canvas canvas = new Canvas(targetBitmap);
            canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
            clear(canvas);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Avoids warnings in M+.
    private void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }
}
