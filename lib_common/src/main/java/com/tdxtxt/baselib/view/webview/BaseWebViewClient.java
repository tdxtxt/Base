package com.tdxtxt.baselib.view.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Looper;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * <pre>
 *     author : ton
 *     time   : 2023/1/3
 *     desc   :
 * </pre>
 */
public class BaseWebViewClient extends WebViewClient {
    private ProgressBar getProgressBar(BaseWebView webView){
        return webView == null ? null : webView.progressBar;
    }

    private void hideProgressBar(final WebView webView){
        if(!(webView instanceof BaseWebView)) return;
        final BaseWebView jxWebView = (BaseWebView) webView;

        if(Looper.getMainLooper().getThread() == Thread.currentThread()){
            ProgressBar progressBar = getProgressBar(jxWebView);
            if(progressBar != null) progressBar.setVisibility(View.GONE);
            return;
        }
        webView.post(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = getProgressBar(jxWebView);
                if(progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showProgressBar(final WebView webView){
        if(!(webView instanceof BaseWebView)) return;
        final BaseWebView jxWebView = (BaseWebView) webView;

        if(Looper.getMainLooper().getThread() == Thread.currentThread()){
            ProgressBar progressBar = getProgressBar(jxWebView);
            if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
            return;
        }

        webView.post(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = getProgressBar(jxWebView);
                if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        hideProgressBar(view);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
        hideProgressBar(view);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
        hideProgressBar(view);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
        handler.proceed();//忽略证书的错误继续Load页面内容，不会显示空白页面
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();
        if(scheme == null){
            if(url.startsWith("http")) view.loadUrl(url);
        }else{
            if(scheme.startsWith("http")){
                view.loadUrl(url);
            }else{
                gotoOtherAppBySchemeProtocol(view.getContext(), uri);
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    private void gotoOtherAppBySchemeProtocol(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
        } catch (Exception e) {
            //通过直接处理抛出的ActivityNotFound异常来确保程序不会崩溃
            e.printStackTrace();
        }
    }
}
