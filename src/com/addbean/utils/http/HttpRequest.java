package com.addbean.utils.http;


import com.addbean.utils.core.BaseException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/8/22.
 */
public class HttpRequest {

    public static HttpMate get(String mUrl) {
        HttpURLConnection urlConnection = null;
        HttpMate mate = new HttpMate();
        try {
            URL url = new URL(mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(false);//该处为true在4.0可能导致get以post方式提交；
//            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android ) Gecko/20100101 Firefox/26.0");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(false);//不执行302主动定向；
//            CYLog.e("请求状态：" + urlConnection.getResponseCode());
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = null;
            if (urlConnection.getResponseCode() == 200) {
                result = readInStream(in);
                mate.setmContent(result);
                mate.setmType(BaseException.ErrorType.SUCCESS);
            }
            if (urlConnection.getResponseCode() == 302 || urlConnection.getResponseCode() == 301) {
                mate.setmContent(urlConnection.getHeaderField("Location"));
                mate.setmType(BaseException.ErrorType.SUCCESS);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            mate.setmType(BaseException.ErrorType.URL_FORMAT_ERROR);
            return mate;
        } catch (IOException e) {
            e.printStackTrace();
            mate.setmType(BaseException.ErrorType.STREAM_ERROR);
            return mate;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return mate;
    }


    public static HttpMate post(String mUrl, Map<String, String> mParmas) {
        HttpURLConnection urlConnection = null;
        HttpMate mate = new HttpMate();
        try {
            URL url = new URL(mUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android Gecko/20100101 Firefox/26.0");
            String postParams = "";
            for (Map.Entry<String, String> entry : mParmas.entrySet()) {
                postParams += "&" + entry.getKey() + "=" + entry.getValue();
            }
            if (postParams==null) {
                postParams = postParams.substring(1, postParams.length());
            }
            byte[] bypes = postParams.toString().getBytes();
            urlConnection.getOutputStream().write(bypes);// 输入参数
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = readInStream(in);
            mate.setmContent(result);
            mate.setmType(BaseException.ErrorType.SUCCESS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mate.setmType(BaseException.ErrorType.URL_FORMAT_ERROR);
            return mate;
        } catch (IOException e) {
            e.printStackTrace();
            mate.setmType(BaseException.ErrorType.STREAM_ERROR);
            return mate;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return mate;
    }


    private static String readInStream(InputStream in) {
        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
