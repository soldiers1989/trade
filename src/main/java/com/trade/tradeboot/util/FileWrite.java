package com.trade.tradeboot.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * create by lizi
 */
public class FileWrite {

    private static List<String> symbols = new ArrayList<>(Arrays.asList("btc_usdt", "eth_usdt",  "bch_usdt", "eos_usdt" , "etc_usdt", "ltc_usdt"));


    public static void write(String fileName, String content) {
        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(fileName, "rw");
            long fileLength = randomFile.length();

            randomFile.seek(fileLength);
            randomFile.writeBytes(content + "\r\n");
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (String sym: symbols) {
            Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol=" + sym + "&type=2hour&size=2000");
            JSONObject jo = JSONObject.parseObject(res.toString().replace("=", ":"));
            JSONArray data = jo.getJSONArray("data");
            System.out.println(data);
            String content = "";
            for (int i=0;i < data.size(); i++) {
                content += (Long) data.getJSONArray(i).get(0) / 1000 + "," +  data.getJSONArray(i).get(1) + "," +
                        data.getJSONArray(i).get(2) + "," +data.getJSONArray(i).get(3) + "," +
                        data.getJSONArray(i).get(4) + "," + data.getJSONArray(i).get(5) + "\n";
            }
            write(sym + ".csv", content);
        }
    }
}
