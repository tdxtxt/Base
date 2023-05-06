package com.tdxtxt.baselib.view.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.tdxtxt.baselib.R;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : ton
 *     time   : 2023/1/3
 *     desc   : 公共WebChromeClient
 * </pre>
 */
public class BaseWebChromeClient extends WebChromeClient {
    /** File upload callback for platform versions prior to Android 5.0 */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /** File upload callback for Android 5.0+ */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected String mUploadableFileTypes = "*/*";
    protected String mLanguageIso3;
    protected static final String CHARSET_DEFAULT = "UTF-8";
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;

    protected Activity getActivity(){
        Activity activity = null;
        if(mActivity != null){
            activity = mActivity.get();
        }
        if(activity == null){
            if(mFragment != null){
                Fragment fragment = mFragment.get();
                if(fragment != null){
                    activity = fragment.getActivity();
                }
            }
        }
        return activity;
    }

    private ProgressBar getProgressBar(BaseWebView webView){
        return webView == null ? null : webView.progressBar;
    }

    private void progressBarChange(final WebView webView, final int newProgress){
        if(!(webView instanceof BaseWebView)) return;
        final BaseWebView baseWebView = (BaseWebView) webView;
        if(Looper.getMainLooper().getThread() == Thread.currentThread()){
            ProgressBar progressBar = getProgressBar(baseWebView);
            if(progressBar != null){
                if (newProgress == 100) {//加载完毕进度条消失
                    progressBar.setVisibility(View.GONE);
                } else {//更新进度
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
            return;
        }

        webView.post(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = getProgressBar(baseWebView);
                if(progressBar != null){
                    if (newProgress == 100) {//加载完毕进度条消失
                        progressBar.setVisibility(View.GONE);
                    } else {//更新进度
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    }
                }
            }
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void fullScreen() {
        Activity activity = getActivity();
        if(activity == null) return;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /*** 视频播放相关的方法 **/
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        fullScreen();
        Activity activity = getActivity();
        if(activity == null) return;
        View decor = activity.getWindow().getDecorView();
        if(decor instanceof FrameLayout){
            view.setId(R.id.webview_video);
            ((FrameLayout) decor).addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onHideCustomView() {
        fullScreen();
        Activity activity = getActivity();
        if(activity == null) return;
        View decor = activity.getWindow().getDecorView();
        if(decor instanceof FrameLayout){
            View view = decor.findViewById(R.id.webview_video);
            if(view != null){
                ((FrameLayout) decor).removeView(view);
            }
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
        progressBarChange(view, newProgress);
    }

    // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, null);
    }

    // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg, acceptType, null);
    }

    // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileInput(uploadMsg, null, false);
    }

    // file upload callback (Android 5.0 (API level 21) -- current) (public method)
    @SuppressWarnings("all")
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= 21) {
            final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;

            openFileInput(null, filePathCallback, allowMultiple);

            return true;
        }
        else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mUploadableFileTypes);

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), REQUEST_CODE_FILE_PICKER);
        }else if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), REQUEST_CODE_FILE_PICKER);
        }
    }

    protected String getFileUploadPromptLabel() {
        try {
            if (mLanguageIso3.equals("zho")) return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            else if (mLanguageIso3.equals("spa")) return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            else if (mLanguageIso3.equals("hin")) return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            else if (mLanguageIso3.equals("ben")) return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            else if (mLanguageIso3.equals("ara")) return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            else if (mLanguageIso3.equals("por")) return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            else if (mLanguageIso3.equals("rus")) return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            else if (mLanguageIso3.equals("jpn")) return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            else if (mLanguageIso3.equals("pan")) return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            else if (mLanguageIso3.equals("deu")) return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            else if (mLanguageIso3.equals("jav")) return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            else if (mLanguageIso3.equals("msa")) return decodeBase64("UGlsaWggc2F0dSBmYWls");
            else if (mLanguageIso3.equals("tel")) return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            else if (mLanguageIso3.equals("vie")) return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            else if (mLanguageIso3.equals("kor")) return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            else if (mLanguageIso3.equals("fra")) return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            else if (mLanguageIso3.equals("mar")) return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            else if (mLanguageIso3.equals("tam")) return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            else if (mLanguageIso3.equals("urd")) return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            else if (mLanguageIso3.equals("fas")) return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            else if (mLanguageIso3.equals("tur")) return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            else if (mLanguageIso3.equals("ita")) return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            else if (mLanguageIso3.equals("tha")) return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            else if (mLanguageIso3.equals("guj")) return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
        }
        catch (Exception ignored) { }

        // return English translation by default
        return "Choose a file";
    }

    protected static String decodeBase64(final String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, CHARSET_DEFAULT);
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == BaseWebChromeClient.REQUEST_CODE_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    }
                    else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                            }
                            else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        }
                        catch (Exception ignored) { }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            }
            else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                }
                else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    protected void bindActivity(Activity activity){
        mActivity = new WeakReference<>(activity);
    }

    protected void bindFragment(Fragment fragment){
        mFragment = new WeakReference<>(fragment);
    }
}
