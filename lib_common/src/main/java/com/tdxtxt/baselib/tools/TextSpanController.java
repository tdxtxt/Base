package com.tdxtxt.baselib.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.Utils;
import com.tdxtxt.baselib.callback.Action;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextSpanController {
    private final SpannableStringBuilder builder;
    private final Deque<Span> stack;

    public TextSpanController() {
        builder = new SpannableStringBuilder();
        stack = new ArrayDeque<>();
    }

    public SpannableStringBuilder getSpannable(){
        return builder;
    }

    public TextSpanController append(String string) {
        if(string == null) string = "";
        builder.append(string);
        return this;
    }

    public TextSpanController append(CharSequence charSequence) {
        builder.append(charSequence);
        return this;
    }

    public TextSpanController append(char c) {
        builder.append(c);
        return this;
    }

    public TextSpanController append(int number) {
        builder.append(String.valueOf(number));
        return this;
    }

    /** Starts {@code span} at the current position in the builder. */
    public TextSpanController pushSpan(Object span) {
        stack.addLast(new Span(builder.length(), span));
        return this;
    }

    public TextSpanController appendImage(int resId){
        builder.append(" ");//用于替换图片的占位字符
        builder.setSpan(new VerticalImageSpan(Utils.getApp(), resId, DynamicDrawableSpan.ALIGN_CENTER), builder.length() - 1, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    public TextSpanController pushClickSpan(final int color, final Action click) {
        return pushSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if(widget instanceof TextView){
                    ((TextView) widget).setHighlightColor(Color.TRANSPARENT);
                }
                if(click != null) click.invoke();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(color);
                ds.setUnderlineText(false);
            }
        });
    }

    public TextSpanController pushSizeSpan(int dp){
        return pushSpan(new AbsoluteSizeSpan(dp, true));
    }

    public TextSpanController pushColorSpan(@ColorInt int color){
        return pushSpan(new ForegroundColorSpan(color));
    }

    public TextSpanController pushBold(boolean isBold){
        return pushSpan(new StyleSpan(isBold ? Typeface.BOLD : Typeface.NORMAL));
    }

    public TextSpanController pushTypefaceSpan(final String typefaceAssetPath){
        return pushSpan(new TypefaceSpan(""){
            Typeface typeface = Typeface.createFromAsset(Utils.getApp().getAssets(), typefaceAssetPath);
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                applyCustomTypeFace(ds);
            }

            @Override
            public void updateMeasureState(@NonNull TextPaint paint) {
                applyCustomTypeFace(paint);
            }

            private void applyCustomTypeFace(Paint paint){
                int oldStyle;
                Typeface old = paint.getTypeface();
                if (old == null) {
                    oldStyle = 0;
                } else {
                    oldStyle = old.getStyle();
                }

                int fake = oldStyle & ~typeface.getStyle();
                if ((fake & Typeface.BOLD) != 0) {
                    paint.setFakeBoldText(true);
                }

                if ((fake & Typeface.ITALIC) != 0) {
                    paint.setTextSkewX(-0.25f);
                }

                paint.setTypeface(typeface);
            }

        });
    }

    /** End the most recently pushed span at the current position in the builder. */
    public TextSpanController popSpan() {
        Span span = stack.removeLast();
        if(span.span instanceof ImageSpan){
            builder.setSpan(span.span, span.start,span.start + 1, android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }else{
            builder.setSpan(span.span, span.start, builder.length(), android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /** Create the final {@link CharSequence}, popping any remaining spans. */
    public CharSequence build() {
        while (!stack.isEmpty()) {
            popSpan();
        }
        return builder;
    }

    private static final class Span {
        final int start;
        final Object span;

        public Span(int start, Object span) {
            this.start = start;
            this.span = span;
        }
    }

    public static final class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Context context, int resourceId, int verticalAlignment) {
            super(context, resourceId, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();
            int transY = 0;
            //获得将要显示的文本高度 - 图片高度除2 = 居中位置+top(换行情况)
            transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
            //偏移画布后开始绘制
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                //获得文字、图片高度
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }
    }
}
