package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * create by lizi
 */
public class HttpRequest {

    public static Map<String,String> httpForGetRequest(String url) {
        Map<String, String> map = new HashMap<String, String>();

        try {
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080));
//			Document jsoup = Jsoup.connect(url).proxy(proxy).ignoreContentType(true).maxBodySize(0).get();
            Document jsoup = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).get();
            Elements title = jsoup.select("body");
            map.put("data", title.get(0).text());
            map.put("code", "200");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("data", "请求错误");
            map.put("code", "400");
            return map;
        }
    }


    public static Map<String,String> httpForPostRequest(String url, JSONObject param) {
        Map<String, String> map = new HashMap<String, String>();

        try {
//            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1086));
            Connection conn = Jsoup.connect(url);
            Document jsoup = conn.ignoreContentType(true).requestBody(param.toString()).maxBodySize(0).header("Content-Type","application/json").post();
//          Document jsoup = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).get();
            Elements title = jsoup.select("body");
            map.put("data", title.get(0).text());
            map.put("code", "200");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("data", "请求错误");
            map.put("code", "400");
            return map;
        }
    }
}
