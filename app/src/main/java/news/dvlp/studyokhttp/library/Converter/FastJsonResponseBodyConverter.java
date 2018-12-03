package news.dvlp.studyokhttp.library.Converter;


import com.alibaba.fastjson.JSON;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import news.dvlp.studyokhttp.library.Callback.HttpError;
import news.dvlp.studyokhttp.library.ConfigHttp.ConfigHttps;
import news.dvlp.studyokhttp.library.Utils;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

/**
 * Created by liubaigang on 2018/12/3.
 */

public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Type type;

    public FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    /*
    * 转换方法
    */
    @Override
    public T convert(ResponseBody value) throws IOException {

        BufferedSource bufferedSource = Okio.buffer(value.source());
        String cacheStr = bufferedSource.readUtf8();
        bufferedSource.close();
        try {
            JSONObject jsonObject = new JSONObject(cacheStr);
            final int code = jsonObject.getInt(Utils.formatNull(ConfigHttps.codeTag,"errorCode"));
            final String msg = jsonObject.getString(Utils.formatNull(ConfigHttps.msgTag,"errorMsg"));
            Object data = jsonObject.get(Utils.formatNull(ConfigHttps.dataTag,"data"));
            Tip tip = new Tip(code, msg);
            if (code != Integer.parseInt(Utils.formatNull(ConfigHttps.successNum+"","0"))) {
                throw new HttpError(msg, tip);
            }
            Class<?> rawType = $Gson$Types.getRawType(type);
            if (Tip.class == rawType) {
                return (T) tip;
            }

            if (data == JSONObject.NULL) {
                //in case
                throw new HttpError("暂无数据", tip);
            }
            //如果是String 直接返回
            if (String.class == rawType) {
                return (T) data.toString();
            }
            //data 为Boolean 如{"msg": "手机号格式错误","code": 0,"data": false}
            if (Boolean.class == rawType && data instanceof Boolean) {
                return (T) data;
            }
            //data 为Integer  如{"msg": "手机号格式错误","code": 0,"data": 12}
            if (Integer.class == rawType && data instanceof Integer) {
                return (T) data;
            }

            T t = JSON.parseObject(data.toString(), type);
            if (t != null) {
                //防止线上接口修改导致反序列化失败奔溃
                return t;
            }
            throw new HttpError("数据异常", tip);
        } catch (JSONException e) {
            throw new HttpError("解析异常", cacheStr);
        }
    }

}
