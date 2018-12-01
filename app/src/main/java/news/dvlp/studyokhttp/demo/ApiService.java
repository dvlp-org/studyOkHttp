package news.dvlp.studyokhttp.demo;



import java.io.File;
import java.util.List;

import news.dvlp.studyokhttp.demo.entity.Article;
import news.dvlp.studyokhttp.demo.entity.LoginInfo;
import news.dvlp.studyokhttp.demo.entity.WXArticle;
import news.dvlp.studyokhttp.library.Callback.Call2;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 创建时间：2018/4/8
 * 编写人： chengxin
 * 功能描述：测试接口
 */
public interface ApiService {
    //登录
    @FormUrlEncoded
    @POST("user/login")
    Call2<LoginInfo> getLogin(@Field("username") String username, @Field("password") String password);

    //获取微信公众号列表
    @GET(Api.getWXarticle)
    Call2<List<WXArticle>> getWXarticle();

    //获取首页文章列表
    @GET("article/list/0/json")
    Call2<Article> getArticle0();

    //下载文件
    @GET("http://shouji.360tpcdn.com/181115/4dc46bd86bef036da927bc59680f514f/com.ss.android.ugc.aweme_330.apk")
    Call2<File> loadDouYinApk();
}


