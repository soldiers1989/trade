package com.trade.tradeboot.trade;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trade.tradeboot.util.HttpRequest;
import com.trade.tradeboot.util.MathCaclateUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownPercent {

    private List<String> symbols = new ArrayList<>(Arrays.asList("btc_usdt", "eth_usdt",  "bch_usdt", "eos_usdt" , "etc_usdt", "ltc_usdt"));

    private Map<String,Object> result = new HashMap<String, Object>() {
        {
            put("btc_usdt",0D);
            put("eth_usdt",0D);
            put("bch_usdt",0D);
            put("eos_usdt",0D);
            put("etc_usdt",0D);
            put("ltc_usdt",0D);
        }
    };

    public void main() {

        for (String sym:symbols) {
            Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol="+ sym +"&type=2hour&size=2000");
            JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
            JSONArray tmp = jo.getJSONArray("data");

            List tmp_list = new ArrayList();

            List<String> time_list = new ArrayList<>();


            Map<BigDecimal, String> timeAndChange = new HashMap<>();

            for (int i=0; i<tmp.size();i++) {
                if (tmp.getJSONArray(i).getBigDecimal(3).compareTo(tmp.getJSONArray(i).getBigDecimal(2)) < 0) {
//                    System.out.println(MathCaclateUtil.formalDate((Long) tmp.getJSONArray(i).get(0)));
//                    System.out.println((tmp.getJSONArray(i).getBigDecimal(2).subtract(tmp.getJSONArray(i).getBigDecimal(3)))
//                    .divide(tmp.getJSONArray(i).getBigDecimal(3), BigDecimal.ROUND_HALF_UP));
                    tmp_list.add((tmp.getJSONArray(i).getBigDecimal(2).subtract(tmp.getJSONArray(i).getBigDecimal(3)))
                            .divide(tmp.getJSONArray(i).getBigDecimal(3), BigDecimal.ROUND_HALF_UP));
                    time_list.add(MathCaclateUtil.formalDate((Long) tmp.getJSONArray(i).get(0)));
                    timeAndChange.put((tmp.getJSONArray(i).getBigDecimal(2).subtract(tmp.getJSONArray(i).getBigDecimal(3)))
                            .divide(tmp.getJSONArray(i).getBigDecimal(3), BigDecimal.ROUND_HALF_UP), MathCaclateUtil.formalDate((Long) tmp.getJSONArray(i).get(0)));
                }
            }
            Collections.sort(tmp_list);
            System.out.println(sym + ":" + timeAndChange.get(tmp_list.get((int) (tmp_list.size() * 0.9))));
            result.put(sym,tmp_list.get((int) (tmp_list.size() * 0.9)));
        }
        System.out.println(result);
    }

    public static void main(String[] args) {
//        DownPercent downPercent = new DownPercent();
//        downPercent.main();
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int x = 0; x <8; x ++) {
            try {
                Runnable run = () -> {
                    while (true) {
                        Map<String, String> res = HttpRequest.httpForGetRequest("https://www.hex.com");
                        System.out.println(res);
                    }
                };
                exec.execute(run);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
