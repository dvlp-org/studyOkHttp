package news.dvlp.studyokhttp.library.Callback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonSyntaxException;

import news.dvlp.studyokhttp.library.ControllerView.ILoadingView;


/**
 * 最终返回请求：包含 loding 状态
 * 请求loding，包装
 */
public abstract class CallbackAnim<T> extends Callback2<T> {
    private ILoadingView mLoadingView;

    public CallbackAnim(@Nullable ILoadingView loadingView) {
        this.mLoadingView = loadingView;
    }

    @Override
    public void onStart(Call2<T> call2) {
        super.onStart(call2);
        if (mLoadingView != null)
            mLoadingView.showLoading();
    }

    @Override
    public void onCompleted(Call2<T> call2) {
        super.onCompleted(call2);
        if (mLoadingView != null)
            mLoadingView.hideLoading();
    }

    @NonNull
    @Override
    public HttpError parseThrowable(Call2<T> call2, Throwable t) {
        HttpError filterError;
        if (t instanceof JsonSyntaxException) {
            filterError = new HttpError("解析异常", t);
        } else {
            filterError = super.parseThrowable(call2, t);
        }
        return filterError;
    }
}
