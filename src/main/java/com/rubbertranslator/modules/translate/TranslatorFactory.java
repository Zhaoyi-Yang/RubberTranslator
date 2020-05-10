package com.rubbertranslator.modules.translate;

import com.rubbertranslator.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.modules.translate.google.GoogleTranslator;
import com.rubbertranslator.modules.translate.youdao.YoudaoTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 15:26
 * 翻译模块向外提供的接口
 */
public class TranslatorFactory {

    private final Map<TranslatorType, AbstractTranslator> translatorEngineMap = new HashMap<>();

    private TranslatorType engineType = TranslatorType.GOOGLE;


    public void setEngineType(TranslatorType type){
        engineType = type;
    }

    public String translate(Language source, Language dest, String text){
        if(!translatorEngineMap.containsKey(engineType)){
            instanceTranslatorEngine(engineType);
        }
        AbstractTranslator translatorEngine = translatorEngineMap.get(engineType);
        return translatorEngine.translate(source,dest,text);
    }


    public void addTranslator(TranslatorType type, AbstractTranslator translator){
        translatorEngineMap.put(type,translator);
    }

    /**
     * 用户没有主动添加翻译引擎，则采用默认设置
     * @param type
     */
    private void instanceTranslatorEngine(TranslatorType type){
        // 翻译接口多的话，可以改用反射+映射关系表
        // 但是不多，直接用switch判断即可
        switch (type){
            case BAIDU:
                translatorEngineMap.put(TranslatorType.BAIDU,new BaiduTranslator());break;
            case GOOGLE:
                translatorEngineMap.put(TranslatorType.GOOGLE,new GoogleTranslator());break;
            case YOUDAO:
                translatorEngineMap.put(TranslatorType.YOUDAO,new YoudaoTranslator());break;
        }
    }

}
