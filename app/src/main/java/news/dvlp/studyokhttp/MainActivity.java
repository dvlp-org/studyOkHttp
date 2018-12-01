package news.dvlp.studyokhttp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.List;

import news.dvlp.studyokhttp.demo.ApiService;
import news.dvlp.studyokhttp.demo.entity.Article;
import news.dvlp.studyokhttp.demo.entity.LoginInfo;
import news.dvlp.studyokhttp.demo.entity.WXArticle;
import news.dvlp.studyokhttp.library.CallMana.CallManager;
import news.dvlp.studyokhttp.library.Callback.Call2;
import news.dvlp.studyokhttp.library.Callback.Callback2;
import news.dvlp.studyokhttp.library.Callback.CallbackAnim;
import news.dvlp.studyokhttp.library.Callback.HttpError;
import news.dvlp.studyokhttp.library.ControllerView.ILoadingView;
import news.dvlp.studyokhttp.library.Converter.FileConverterFactory;
import news.dvlp.studyokhttp.library.RetrofitManager;
import news.dvlp.studyokhttp.library.progress.ProgressInterceptor;
import news.dvlp.studyokhttp.library.progress.ProgressListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;


public class MainActivity extends Activity implements ILoadingView {
    TextView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = findViewById(R.id.progress);
        progressView.setTextColor(Color.RED);

    }



    public void login(View view) {
        RetrofitManager.create(ApiService.class)
                .getLogin("singleman", "123456")
                .enqueue(hashCode(), new CallbackAnim<LoginInfo>(this) {
                    @Override
                    public void onError(Call2<LoginInfo> call2, HttpError error) {
                        Toast.makeText(MainActivity.this, error.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Call2<LoginInfo> call2, LoginInfo response) {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void wxarticle(View view) {
        RetrofitManager.create(ApiService.class)
                .getWXarticle()
                .enqueue(hashCode(), new CallbackAnim<List<WXArticle>>(this) {
                    @Override
                    public void onError(Call2<List<WXArticle>> call2, HttpError error) {
                        Toast.makeText(MainActivity.this, error.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Call2<List<WXArticle>> call2, List<WXArticle> response) {
                        Toast.makeText(MainActivity.this, "获取公众号列表成功", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void article0(View view) {
        RetrofitManager.create(ApiService.class)
                .getArticle0()
                .enqueue(hashCode(), new CallbackAnim<Article>(this) {
                    @Override
                    public void onError(Call2<Article> call2, HttpError error) {
                        Toast.makeText(MainActivity.this, error.msg, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onSuccess(Call2<Article> call2, Article response) {
                        Toast.makeText(MainActivity.this, "获取列表成功", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    static final String TAG_LOAD_APK = "loadApk";

    public void download(View view) {
        final Button button = (Button) view;
        if (button.getText().equals("取消下载")) {
            CallManager.getInstance().cancel(TAG_LOAD_APK);
            return;
        }

        String filePath = new File(getApplicationContext().getExternalCacheDir(), "test_douyin.apk").getPath();
        //构建可以监听进度的client
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(getProgressInterceptor()).build();

        //构建可以下载文件的client
        Retrofit retrofit = RetrofitManager.retrofit()
                .newBuilder()
                .callFactory(client)
                .addConverterFactory(new FileConverterFactory(filePath))
                .build();

        retrofit.create(ApiService.class)
                .loadDouYinApk()
                .enqueue(TAG_LOAD_APK, new Callback2<File>() {
                    @Override
                    public void onStart(Call2<File> call2) {
                        super.onStart(call2);
                        button.setText("取消下载");
                    }

                    @Override
                    public void onError(Call2<File> call2, HttpError error) {
                        progressView.setText("下载进度:0");
                        Toast.makeText(MainActivity.this, error.msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Call2<File> call2, File response) {

                    }

                    @Override
                    public void onCancel(Call2<File> call2) {
                        super.onCancel(call2);
                        progressView.setText("下载进度:0");

                        button.setText("下载抖音apk文件");
                    }

                    @Override
                    public void onCompleted(Call2<File> call2) {
                        super.onCompleted(call2);
                        button.setText("下载完成");
                    }
                });
    }

    private ProgressInterceptor getProgressInterceptor() {
        return new ProgressInterceptor(new ProgressListener() {
            @Override
            public void onUpload(Request request, long progress, long contentLength, boolean done) {

            }

            @Override
            public void onDownload(Request request, final long progress, final long contentLength, boolean done) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressView.setText("下载进度:"+((int) (progress * 100f / contentLength)));

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //hashCode() 能保证唯一性，取消当前页面所发起的所有请求，只要
        // enqueue(tag, callback2) 传入的是对应的hashCode() 即可
        CallManager.getInstance().cancel(hashCode());
        //取消下载文件
        CallManager.getInstance().cancel(TAG_LOAD_APK);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}