package com.tdxtxt.baselib.callback;

import android.text.TextUtils;

import androidx.annotation.ColorInt;

import com.tdxtxt.baselib.tools.TextSpanController;

public class MenuCallBack {
    public CharSequence menuText;
    public int icon;
    public Action click;

    public MenuCallBack(){

    }

    public MenuCallBack(String value){
        this.menuText = value;
        if(this.menuText == null) this.menuText = "";
    }

    public MenuCallBack(int value, Action click){
        this.icon = value;
        this.click = click;
    }

    public MenuCallBack(String value, Action click){
        this.menuText = value;
        this.click = click;
        if(this.menuText == null) this.menuText = "";
    }

    public MenuCallBack(String value, int color){
        if(value != null) this.menuText = new TextSpanController().pushColorSpan(color).append(value).build();
        if(this.menuText == null) this.menuText = "";
    }

    public MenuCallBack(String value, @ColorInt int color, Action click){
        if(value != null) this.menuText = new TextSpanController().pushColorSpan(color).append(value).build();
        this.click = click;
        if(this.menuText == null) this.menuText = "";
    }

    public boolean isTextMenu(){
        return !TextUtils.isEmpty(menuText);
    }

    public boolean isIconMenu(){
        return icon > 0;
    }
}
