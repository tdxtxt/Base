package com.tdxtxt.baselib.rx;


import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * <pre>
 *     author : 唐德祥
 *     time   : 2022/11/4
 *     desc   : 拦截rxjava 所有未捕捉的错误
 * </pre>
 */
public class RxErrorHandler implements Consumer<Throwable> {
    public static void install(){
        //拦截rxjava 所有未捕捉的错误
        RxJavaPlugins.setErrorHandler(new RxErrorHandler());
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        throwable.printStackTrace();
    }
}