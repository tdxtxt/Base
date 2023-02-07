package com.tdxtxt.baselib.view.textview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.tdxtxt.baselib.R;

public class RoundStyleTextView extends AppCompatTextView {
    private int rtvBorderWidth;

    public RoundStyleTextView(Context context) {
        this(context, null);
    }

    public RoundStyleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null){

            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundStyleTextView);

            rtvBorderWidth = attributes.getDimensionPixelSize(R.styleable.RoundStyleTextView_strokeWidth, 0);
            int rtvBorderColor = attributes.getColor(R.styleable.RoundStyleTextView_strokeColor, Color.BLACK);
            float radius = attributes.getDimension(R.styleable.RoundStyleTextView_radius, 0);
            float leftRadius = attributes.getDimension(R.styleable.RoundStyleTextView_leftRadius, 0);
            float rightRadius = attributes.getDimension(R.styleable.RoundStyleTextView_rightRadius, 0);
            float topRadius = attributes.getDimension(R.styleable.RoundStyleTextView_topRadius, 0);
            float bottomRadius = attributes.getDimension(R.styleable.RoundStyleTextView_bottomRadius, 0);
            float topLeftRadius = attributes.getDimension(R.styleable.RoundStyleTextView_topLeftRadius, topRadius == 0 ? leftRadius : topRadius);
            float topRightRadius = attributes.getDimension(R.styleable.RoundStyleTextView_topRightRadius, topRadius == 0 ? rightRadius : topRadius);
            float bottomLeftRadius = attributes.getDimension(R.styleable.RoundStyleTextView_bottomLeftRadius, bottomRadius == 0 ? leftRadius : bottomRadius);
            float bottomRightRadius = attributes.getDimension(R.styleable.RoundStyleTextView_bottomRightRadius, bottomRadius == 0 ? rightRadius : bottomRadius);
            int rtvBgColor = attributes.getColor(R.styleable.RoundStyleTextView_bgColor, Color.WHITE);
            int startBgColor = attributes.getColor(R.styleable.RoundStyleTextView_startBgColor, 0);
            int endBgColor = attributes.getColor(R.styleable.RoundStyleTextView_endBgColor, 0);
            attributes.recycle();

            GradientDrawable gd;
            if(startBgColor != 0 && endBgColor != 0){
                gd =  new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startBgColor, endBgColor});
                gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            }else{
                gd = new GradientDrawable();//创建drawable
                gd.setColor(rtvBgColor);
            }
            if(radius > 0){
                gd.setCornerRadius(radius);
            }else{
                float[] radiusx = new float[]{
                        topLeftRadius, topLeftRadius,
                        topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius,
                        bottomLeftRadius, bottomLeftRadius
                };
                gd.setCornerRadii(radiusx);
            }

            if (rtvBorderWidth > 0) {
                gd.setStroke(rtvBorderWidth, rtvBorderColor);
            }

            this.setBackground(gd);
        }
    }

    public void setBackgroungColor(@ColorInt int color) {
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        if(myGrad != null) myGrad.setColor(color);
    }

    public void setStrokeColor(@ColorInt int color) {
        if(rtvBorderWidth <= 0) return;
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        if(myGrad != null) myGrad.setStroke(rtvBorderWidth, color);
    }
}
