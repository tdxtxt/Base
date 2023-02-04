package com.tdxtxt.baselib.rx.transformer;

import android.annotation.SuppressLint;

import com.tdxtxt.baselib.dialog.impl.ProgressDialog;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

@SuppressLint("CheckResult")
public class ProgressTransformer<T> implements ObservableTransformer<T, T>, FlowableTransformer<T, T> {
    ProgressDialog progressDialog;
    boolean bindDialog = true;//是否绑定进度条

    public ProgressTransformer(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public ProgressTransformer(ProgressDialog progressDialog, boolean bindDialog) {
        this.progressDialog = progressDialog;
        this. bindDialog = bindDialog;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        if (progressDialog != null) {
                            if (bindDialog)
                                progressDialog.setCancelListener(new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        disposable.dispose();
                                        return null;
                                    }
                                });
                            progressDialog.show();
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                });
    }

    @NonNull
    @Override
    public Publisher<T> apply(@NonNull Flowable<T> upstream) {
        return upstream.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(final Subscription subscription) throws Exception {
                        if (progressDialog != null) {
                            if (bindDialog)
                                progressDialog.setCancelListener(new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        subscription.cancel();
                                        return null;
                                    }
                                });
                            progressDialog.show();
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (progressDialog != null) progressDialog.dismiss();
                    }
                });
    }
}
