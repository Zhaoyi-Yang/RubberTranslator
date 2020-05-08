package com.rubbertranslator.modules.translate.baidu;

import com.rubbertranslator.modules.translate.ITranslator;
import com.rubbertranslator.test.Configuration;
import com.rubbertranslator.utils.JsonUtil;
import com.rubbertranslator.utils.DigestUtil;
import com.rubbertranslator.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 14:37
 */
public class BaiduTranslator implements ITranslator {
    /**
     * baidu翻译
     * @param source 源语言
     * @param dest 目标语言
     * @param text 需要翻译的文本
     * @return null，如果翻译不成功
     *         翻译后的文本
     */
    @Override
    public String translate(String source, String dest, String text) {
        // 百度中文特殊处理
        String translatedText = null;
        try {
            BaiduTranslationResult baiduTranslateResult = doTranslate(source,dest,text);
            if(baiduTranslateResult != null){
                translatedText = mergeTranslatedText(baiduTranslateResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        Logger.getLogger(this.getClass().getName()).info("Baidu："+translatedText);
        return translatedText;
    }

    private BaiduTranslationResult doTranslate(String source, String dest, String text) throws IOException {
        String URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        String APP_ID = Configuration.BAIDU_TRANSLATE_API_KEY;
        String SECRETE_KEY = Configuration.BAIDU_TRANSLATE_SECRET_KEY;
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        // 加密前的原文
        String src = APP_ID + text + salt + SECRETE_KEY;
        String sign = DigestUtil.md5(src);

        RequestBody requestBody = new FormBody.Builder()
                .add("q", text)
                .add("from",source)
                .add("to",dest)
                .add("appid", APP_ID)
                .add("salt",salt)
                .add("sign",sign)
                .build();
        String json = OkHttpUtil.syncPostRequest(URL,requestBody);
        Logger.getLogger(this.getClass().getName()).info(json);
        BaiduTranslationResult deserialize = JsonUtil.deserialize(json, BaiduTranslationResult.class);
        if(deserialize != null && deserialize.getErrorCode() == null){
            return deserialize;
        }else{
            return null;
        }
    }

    /**
     * 合并翻译后的文本
     * @param result 百度翻译结果对应
     * @return  合并后的文本
     */
    private String mergeTranslatedText(BaiduTranslationResult result){
        StringBuilder sb = new StringBuilder();
        for(BaiduTranslationResult.TransResultItem item : result.getTransResult()){
            sb.append(item.getDst()).append("\n");
        }
        return sb.toString();
    }

}