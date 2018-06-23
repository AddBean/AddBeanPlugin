package com.addbean.action;

import com.addbean.model.HttpHelper;
import com.addbean.model.TranslateModel;
import com.addbean.utils.http.OnHttpListener;
import com.google.gson.Gson;

import java.net.URLEncoder;

public class TestMain {

    public static void main(String[] args){
        HttpHelper.translate(URLEncoder.encode("测试"), new OnHttpListener<TranslateModel>() {
            @Override
            public void onSuccess(TranslateModel data) {
                StringBuilder result=new StringBuilder();
                result.append("\""+data.getQuery()+"\"\n");
                result.append("释义："+data.getTranslation()+"\n");
                for (int i = 0; i <data.getBasic().getExplains().size() ; i++) {
                    result.append("\t"+data.getBasic().getExplains().get(i)+"\n");
                }
                result.append("更多：\n");
                for (int i = 0; i <data.getWeb().size() ; i++) {
                    result.append("\t"+data.getWeb().get(i).getKey()+"："+data.getWeb().get(i).getValue()+"\n");
                }
                System.out.print(result);
            }
        });
    }
}
