package news.dvlp.studyokhttp.demo;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


import news.dvlp.studyokhttp.BuildConfig;
import news.dvlp.studyokhttp.library.Interceptor.HttpLoggingInterceptor;
import news.dvlp.studyokhttp.library.RetrofitManager;

/**
 * 创建时间：2018/8/2
 * 编写人： karler
 * 功能描述：
 */
public class OKApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy
                .newBuilder()
                .logStrategy(new LogCatStrategy())
                .tag("OKHTTP_LOG")
                .methodCount(1).showThreadInfo(false).build()) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });


        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.d(message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
      /*  Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://wanandroid.com/")
                .callFactory(new OkHttpClient.Builder()
                        .addNetworkInterceptor(httpLoggingInterceptor)
                        .build())
                .addCallAdapterFactory(ExecutorCallAdapterFactory.INSTANCE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitManager.init(retrofit);*/
        RetrofitManager.init(Api.API_BASE);

    }

    public static class LogCatStrategy implements LogStrategy {

        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message) {
            Log.println(priority, randomKey() + tag, message);
        }

        private int last;

        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);
        }
    }
}
