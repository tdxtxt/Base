package com.tdxtxt.net.observer;

import com.tdxtxt.net.NetMgr;
import com.tdxtxt.net.config.NetProvider;
import com.tdxtxt.net.model.AbsResponse;
import org.jetbrains.annotations.NotNull;
import io.reactivex.observers.DisposableObserver;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-09-14
 *     desc   : 这里不用kotlin，是因为没有边界的泛型R导致无法重写onNext方法，kotlin高版本会有这个问题
 * </pre>
 */
public abstract class AbsObserverNetapi<R> extends DisposableObserver<R>{
    public abstract String host();
    /**
     * errorBody:响应报文中的ErrorBody数据内容
     */
    public abstract void onFailure(int errorCode, String errorMsg, String errorBody);

    public abstract void onSuccess(R response);

    @Override
    public void onNext(@NotNull R response) {
        onSuccess(response);

        onComplete();
    }

    @Override
    public void onError(@NotNull Throwable e) {
        NetProvider provider = getProvider();
        if(provider != null){
            String message = provider.throwable2Message(e);
            int code = provider.throwable2Code(e);
            AbsResponse data = provider.throwable2Response(e);
            String errorBody  = provider.throwable2ErrorBody(e);
            provider.handleError(data, code, message);
            onFailure(code, message, errorBody);
        }else{
            onFailure(-999, "provider is null", "");
        }
        onComplete();
    }

    @Override
    public void onComplete() {

    }

    public NetProvider getProvider() {
        return NetMgr.getProvider(host());
    }
}
