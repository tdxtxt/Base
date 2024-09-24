package com.tdxtxt.baselib.view.html;

import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.tdxtxt.baselib.tools.ToastHelper;
import com.tdxtxt.baselib.view.html.from.HtmlFormatter;
import com.tdxtxt.baselib.view.html.from.HtmlFormatterBuilder;
import com.tdxtxt.baselib.view.html.from.OnClickImgTagListener;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/26
 *     desc   : https://gitee.com/adminfun/HTMLTextView/tree/master
 *              https://github.com/SufficientlySecure/html-textview
 * </pre>
 */
public class HHtml {
    private HHtml() {
    }
    public static void setHtml(TextView textView, String html){
        setHtml(textView, html, true);
    }
    public static void setHtml(TextView textView, String html, boolean matchParentWidth){
        if(textView == null) return;
        if(html == null || html.length() == 0) return;
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);
        Spanned spanned = HtmlFormatter.formatHtml(new HtmlFormatterBuilder()
                .setHtml(html)
                .setImageGetter(new HtmlImageGetter(new WeakReference<>(textView), matchParentWidth)));

//        Spanned spanned = removeHtmlBottomPadding(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT,
//                new HtmlImageGetter(new WeakReference<>(textView), matchParentWidth), null));
        textView.setText(spanned);
    }
    private static Spanned removeHtmlBottomPadding(@Nullable Spanned text) {
        if (text == null) {
            return null;
        }

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = (Spanned) text.subSequence(0, text.length() - 1);
        }
        return text;
    }
}
