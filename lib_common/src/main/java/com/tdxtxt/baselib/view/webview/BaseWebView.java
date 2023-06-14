package com.tdxtxt.baselib.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.SizeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tdxtxt.baselib.R;
import com.tdxtxt.baselib.callback.LifecycleMethod;
import com.tdxtxt.baselib.callback.LifecycleObserver;

/**
 * <pre>
 *     author : ton
 *     time   : 2022/12/28
 *     desc   :
 * </pre>
 */
public class BaseWebView extends WebView {
    protected ProgressBar progressBar;//进度条
    protected WebChromeClient chromeClient;

    public BaseWebView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public BaseWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        //初始化进度条
        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(2)));
        progressBar.setIndeterminate(false);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.baselib_webview_progress));
        progressBar.setProgress(3);
        progressBar.setVisibility(View.GONE);
        //把进度条加到Webview中
        addView(progressBar);
        initWebView(context);
    }

    private void initWebView(Context context){
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);

        webSetting.setAppCacheEnabled(true);
        String appCachePath = context.getCacheDir().getAbsolutePath();
        webSetting.setAppCachePath(appCachePath);
        webSetting.setDatabaseEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(1024 * 1024 * 8);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        WebView.setWebContentsDebuggingEnabled(true);

        webSetting.setSupportMultipleWindows(false);
        webSetting.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSetting.setMixedContentMode(0);
        }

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        cookieManager.setAcceptCookie(true);

        setWebViewClient(new BaseWebViewClient());

        setWebChromeClient(new BaseWebChromeClient());
    }

    public void loadHtmlBody(String bodyHTML){
        loadDataWithBaseURL(null, addHead(bodyHTML), "text/html", "utf-8",null);
    }

    protected String addHead(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:100%; height:auto;}*{margin:0px;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    @Override
    public void setWebChromeClient(@Nullable WebChromeClient client) {
        this.chromeClient = client;
        super.setWebChromeClient(client);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (getParentRefreshLayout() != null) {
            getParentRefreshLayout().setEnableRefresh(clampedY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParentRefreshLayout() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParentRefreshLayout().setEnableRefresh(false);
        }
        return super.onTouchEvent(event);
    }

    private SmartRefreshLayout getParentRefreshLayout(){
        ViewParent viewParent = getParent();
        if(viewParent instanceof SmartRefreshLayout){
            return (SmartRefreshLayout) viewParent;
        }else if(viewParent instanceof ViewGroup){
            ViewParent viewParentParent = viewParent.getParent();
            if(viewParentParent instanceof SmartRefreshLayout){
                return (SmartRefreshLayout) viewParentParent;
            }
        }
        return null;
    }



    public boolean onBackPressed() {
        if (canGoBack()) {
            goBack();
            return false;
        }
        else {
            return true;
        }
    }

//    public void onResume() {
//        if (Build.VERSION.SDK_INT >= 11) {
//            super.onResume();
//        }
//        resumeTimers();
//    }
//
//    public void onPause() {
//        pauseTimers();
//        if (Build.VERSION.SDK_INT >= 11) {
//            super.onPause();
//        }
//    }

    public void onDestroy(){
        try {
            ((ViewGroup) getParent()).removeView(this);
        }
        catch (Exception ignored) { }

//         then try to remove all child views from this view
        try {
            removeAllViews();
        }
        catch (Exception ignored) { }

        // and finally destroy this view
        destroy();
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if(chromeClient instanceof BaseWebChromeClient) ((BaseWebChromeClient) chromeClient).onActivityResult(requestCode, resultCode, intent);
    }

    private LifecycleObserver lifecycleObserver;
    public void bindActivity(Activity activity){
        if(chromeClient instanceof BaseWebChromeClient){
            ((BaseWebChromeClient) chromeClient).bindActivity(activity);
        }
        if(activity instanceof FragmentActivity){
            initLifecycle();
            ((FragmentActivity) activity).getLifecycle().removeObserver(lifecycleObserver);
            ((FragmentActivity) activity).getLifecycle().addObserver(lifecycleObserver);
        }
    }

    public void bindFragment(Fragment fragment){
        if(chromeClient instanceof BaseWebChromeClient){
            ((BaseWebChromeClient) chromeClient).bindFragment(fragment);
        }
        if(fragment != null){
            initLifecycle();
            fragment.getLifecycle().removeObserver(lifecycleObserver);
            fragment.getLifecycle().addObserver(lifecycleObserver);
        }
    }

    private void initLifecycle(){
        if(lifecycleObserver == null) lifecycleObserver = new LifecycleObserver(new LifecycleMethod() {
            //这两个方法调用可能会导致一些莫名奇怪的问题，一次注释掉
//            @Override
//            public void onResume() {
//                BaseWebView.this.onResume();
//            }
//
//            @Override
//            public void onPause() {
//                BaseWebView.this.onPause();
//            }

            @Override
            public void onDestroy() {
                BaseWebView.this.onDestroy();
            }
        });
    }
}
