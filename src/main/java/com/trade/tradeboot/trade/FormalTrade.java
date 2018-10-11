package com.trade.tradeboot.trade;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.trade.tradeboot.stock.IStockRestApi;
import com.trade.tradeboot.stock.impl.StockRestApi;
import com.trade.tradeboot.util.*;
import org.apache.http.HttpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.trade.tradeboot.staticInfo.OrderInfo.ORDER_NO;
import static com.trade.tradeboot.staticInfo.OrderInfo.ORDER_NO_JACKY;


/**
 * 正式交易
 * create by lizi
 */
//@Service
public class FormalTrade {

    // 币对
    private List<String> symbols = new ArrayList<>(Arrays.asList("btc_usdt", "eth_usdt",  "bch_usdt", "eos_usdt" , "etc_usdt", "ltc_usdt"));
//    private List<String> symbols = new ArrayList<>(Arrays.asList(
//            "btc_usdt"
//    ));


    /**
     * 存买入时间
     */
    private Map<String, Long> buy_time = new HashMap<String, Long>() {
        {
            put("btc_usdt", 0L);
            put("eth_usdt", 0L);
            put("xrp_usdt", 0L);
            put("bch_usdt", 0L);
            put("eos_usdt", 1539106446000L);
            put("ltc_usdt", 0L);
            put("xlm_usdt", 0L);
            put("trx_usdt", 0L);
            put("neo_usdt", 0L);
            put("xmr_usdt", 0L);
            put("etc_usdt", 0L);
        }
    };


    /**
     * 存买点
     */
    private Map<String, Boolean> buy_point = new HashMap<String, Boolean>() {
        {
            put("btc_usdt", false);
            put("eth_usdt", false);
            put("xrp_usdt", false);
            put("bch_usdt", false);
            put("eos_usdt", false);
            put("ltc_usdt", false);
            put("xlm_usdt", false);
            put("trx_usdt", false);
            put("neo_usdt", false);
            put("xmr_usdt", false);
            put("etc_usdt", false);
        }
    };





    private Map<String, Double> last_buy_price = new HashMap<String, Double>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 5.9339D);
            put("ltc_usdt",0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };

    // k线类型
    private String type = "2hour";

    private int K = 25;

    private int D = 25;

    private int J = 25;



    // 方差
    private int buy_var = 10;

    // 买百分比
    private Double buy_percent = 0.15D;

    // 总仓
    private Double total_price = 100000D;

    // 手续费
    private Double rate = 0.0015D;

    // 高点交叉的stochRSI设置
    private int maxK_last = 70;

    private int maxK_now = 70;

    private int maxD_last = 70;

    private int maxD_now = 70;


//    @PostConstruct
//    public void start() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    trade();
//                } catch (Exception e) {
//                    System.out.println("报错啦");
//                    e.printStackTrace();
//                }
//            }
//        }, 1000, 30000);
//    }

//    @Scheduled(cron="0/30 * *  * * ? ")
//    public void  test() {
//        System.out.println("111");
//    }



    @Scheduled(cron="0/30 * *  * * ? ")
    public void trade() throws InterruptedException, IOException, HttpException {


        List<JSONArray> klines = new ArrayList<>();

        List<JSONArray> macd = new ArrayList<>();

        List<JSONArray> kdj = new ArrayList<>();

        List<JSONArray> marsi = new ArrayList<>();

        List<String> symbol_list = new ArrayList<>();


        List<JSONArray> k = new ArrayList<>();

        List<JSONArray> d = new ArrayList<>();


        // 得到所有币种的k线数据
        for (String sym: symbols) {
            Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol="+ sym +"&type=" + type + "&size=100");
            JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
            JSONArray data = jo.getJSONArray("data");
            macd.add(Macd.getMacd(data));
            kdj.add(Kdj.getKdj(data));
            marsi.add(Marsi.getMarsi(data));
            klines.add(data);
            symbol_list.add(sym);
            JSONArray rsi = Rsi.getRsi(data);

            JSONObject param = new JSONObject();
            param.put("rsi", rsi.toString());
            param.put("n","12");
            param.put("m","12");
            param.put("p1","3");
            param.put("p2","3");
            param.put("kline", data);

//            Map<String, String> res1 = HttpRequest.httpForPostRequest("https://wechatpublic.pgrab.cn/getStochRSI", param);
//            JSONObject jo1 = JSONObject.parseObject(res1.toString().replace("=",":"));
//            JSONObject jo2 = jo1.getJSONObject("data").getJSONObject("data");
//            JSONArray k_tmp = jo2.getJSONArray("k");
//            JSONArray d_tmp = jo2.getJSONArray("d");
//            k.add(k_tmp);
//            d.add(d_tmp);

            List<Double> rsi_param = new ArrayList<>();
            for (int xx = 0; xx < rsi.size(); xx++) {
                rsi_param.add(((BigDecimal) rsi.get(xx)).doubleValue());
            }

            Map<String, String> res1 = StochRSI.getStochRSI(JSONArray.parseArray(data.toString()), rsi_param, 12D, 12D, 3D, 3D);
            JSONArray k_tmp = JSONArray.parseArray(res1.get("k"));
            JSONArray d_tmp = JSONArray.parseArray(res1.get("d"));
            k.add(k_tmp);
            d.add(d_tmp);
        }

        for (int i=0; i<symbol_list.size(); i++) {

            String currentTime = MathCaclateUtil.formalDate(System.currentTimeMillis());

//            String currentPrice = (String) ((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(4);
            String currentPrice = "";
            BigDecimal macd_now = (BigDecimal) ((List<Object>) macd.get(i).get(klines.get(i).size() - 1)).get(1);
            BigDecimal macd_last = (BigDecimal) ((List<Object>) macd.get(i).get(klines.get(i).size() - 2)).get(1);
            BigDecimal k_now = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 1)).get(1);
            BigDecimal d_now = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 1)).get(2);
            BigDecimal j_now = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 1)).get(3);
            BigDecimal k_last = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 2)).get(1);
            BigDecimal d_last = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 2)).get(2);
            BigDecimal j_last = (BigDecimal) ((List<Object>) kdj.get(i).get(klines.get(i).size() - 2)).get(3);
            BigDecimal marsi1_now = (BigDecimal) ((List<Object>) marsi.get(i).get(klines.get(i).size() - 1)).get(1);
            BigDecimal marsi2_now = (BigDecimal) ((List<Object>) marsi.get(i).get(klines.get(i).size() - 1)).get(2);
            BigDecimal marsi1_last = (BigDecimal) ((List<Object>) marsi.get(i).get(klines.get(i).size() - 2)).get(1);
            BigDecimal marsi2_last = (BigDecimal) ((List<Object>) marsi.get(i).get(klines.get(i).size() - 2)).get(2);
            BigDecimal stoch1_now = (BigDecimal) ((List<Object>) k.get(i).get(klines.get(i).size() - 1)).get(1);
            BigDecimal stoch2_now = (BigDecimal) ((List<Object>) d.get(i).get(klines.get(i).size() - 1)).get(1);
            BigDecimal stoch1_last = (BigDecimal) ((List<Object>) k.get(i).get(klines.get(i).size() - 2)).get(1);
            BigDecimal stoch2_last = (BigDecimal) ((List<Object>) d.get(i).get(klines.get(i).size() - 2)).get(1);

            double[] var_now = new double[2];
            double[] var_last = new double[2];
            var_now[0] = k_now.doubleValue();
            var_now[1] = d_now.doubleValue();

            var_last[0] = k_last.doubleValue();
            var_last[1] = d_last.doubleValue();

            if (System.currentTimeMillis() - (long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(0) > 2 * 1000 * 60 * 60L) {
                // 说明此时k线不是最新的， 不能用
                continue;
            }


            System.out.println("=========");
            System.out.println(symbol_list.get(i));
            System.out.println(currentTime);
            System.out.println(stoch1_last);
            System.out.println(stoch2_last);
            System.out.println(stoch1_now);
            System.out.println(stoch2_now);
            System.out.println(k_last);
            System.out.println(k_now);
            System.out.println(macd_now);
            System.out.println(macd_last);

            String user_usdt_num = "";

            String jacky_usdt_num = "";

            String user_symbol_num = "";

            String jacky_symbol_num = "";

            // 通用GET方法
            IStockRestApi stockGet = new StockRestApi(Config.URL_PREX);

            // 通用POST
            IStockRestApi stockPost = new StockRestApi(Config.URL_PREX, Config.API_KEY, Config.SECRET_KEY);

            // jacky 大号
            IStockRestApi stockPost_jacky = new StockRestApi(Config.URL_PREX, Config.API_KEY1, Config.SECRET_KEY1);


//            stockPost_jacky.order_info("btc_usd","891076675");
            try {

                String result1 = stockPost.userinfo();

                String result_jacky = stockPost_jacky.userinfo();

                user_usdt_num = JSONObject.parseObject(result1).getJSONObject("info").getJSONObject("funds").getJSONObject("free").getString("usdt");

                jacky_usdt_num = JSONObject.parseObject(result_jacky).getJSONObject("info").getJSONObject("funds").getJSONObject("free").getString("usdt");

                String result = stockGet.ticker(symbol_list.get(i));
                currentPrice = JSONObject.parseObject(result).getJSONObject("ticker").getString("last");


                user_symbol_num = JSONObject.parseObject(result1).getJSONObject("info").getJSONObject("funds").getJSONObject("free").getString(symbol_list.get(i).split("_")[0]);
                jacky_symbol_num = JSONObject.parseObject(result_jacky).getJSONObject("info").getJSONObject("funds").getJSONObject("free").getString(symbol_list.get(i).split("_")[0]);
            } catch (HttpException | IOException e) {
                e.printStackTrace();
                continue;
            }



            // 买入逻辑
            if (buy_point.get(symbol_list.get(i)) && System.currentTimeMillis()  - buy_time.get(symbol_list.get(i)) > 2 * 60 * 60  * 1000L) {
                    if (stoch1_now.compareTo(new BigDecimal(15)) > 0) {
                    // 达到买入条件

                    try {
                        double buy_num = MathCaclateUtil.divide(MathCaclateUtil.multiply(Double.parseDouble(user_usdt_num), buy_percent , BigDecimal.ROUND_HALF_UP),
                                Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP);


                        double buy_num_jacky = MathCaclateUtil.divide(MathCaclateUtil.multiply(Double.parseDouble(jacky_usdt_num), buy_percent , BigDecimal.ROUND_HALF_UP),
                                Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP);

                        String tradeResult = stockPost.trade(symbol_list.get(i), "buy", currentPrice, Double.toString(buy_num));

                        String tradeResult_jacky = stockPost_jacky.trade(symbol_list.get(i), "buy", currentPrice, Double.toString(buy_num_jacky));

                        System.out.println(tradeResult);
                        System.out.println("jacky::"+tradeResult_jacky);
                        System.out.println(currentTime);

                        total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                        last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));

                        FileWrite.write("../buy_summary.txt", "buy_time:" + currentTime + "buy_price:" +
                                currentPrice + "buy_coin_name:" + symbol_list.get(i) + "tradeResult:" + tradeResult + "tradeResult_jacky:" + tradeResult_jacky);

                        buy_point.put(symbol_list.get(i), false);

                        buy_time.put(symbol_list.get(i), (Long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(0));


                        // 加入订单检测队列
                        Map<String, Double> tmp1 = new HashMap<>();
                        tmp1.put(symbol_list.get(i), Double.parseDouble(currentPrice));

                        Map<Long,Map<String, Double>> tmp2 = new HashMap<>();
                        tmp2.put(System.currentTimeMillis(), tmp1);

                        ORDER_NO_JACKY.put(JSONObject.parseObject(tradeResult_jacky).get("order_id").toString(), tmp2);

                        ORDER_NO.put(JSONObject.parseObject(tradeResult).get("order_id").toString(), tmp2);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (macd_now.compareTo(macd_last) >= 0 && stoch1_last.compareTo(stoch2_last) < 0 && stoch1_now.compareTo(stoch2_now) >= 0
                    && stoch1_last.compareTo(new BigDecimal(maxK_last)) < 0 && stoch2_last.compareTo(new BigDecimal(maxD_last)) < 0
                    && stoch1_now.compareTo(new BigDecimal(maxK_now)) < 0  && stoch1_now.compareTo(new BigDecimal(maxD_now)) < 0
                    && System.currentTimeMillis()  - buy_time.get(symbol_list.get(i)) > 2 * 60 * 60  * 1000L) {
//                        if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
//                                && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
//                        if ((new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
                if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
                        && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
                        || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1) || (k_now.compareTo(d_now) == -1
                        && k_last.compareTo(d_last) == -1 && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == -1)) {
                    if (stoch1_now.compareTo(new BigDecimal(15)) == 1) {
                        // 达到买入条件

                        try {
                            double buy_num = MathCaclateUtil.divide(MathCaclateUtil.multiply(Double.parseDouble(user_usdt_num), buy_percent , BigDecimal.ROUND_HALF_UP),
                                    Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP);

                            double buy_num_jacky = MathCaclateUtil.divide(MathCaclateUtil.multiply(Double.parseDouble(jacky_usdt_num), buy_percent , BigDecimal.ROUND_HALF_UP),
                                    Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP);


                            String tradeResult = stockPost.trade(symbol_list.get(i), "buy", currentPrice, Double.toString(buy_num));

                            String tradeResult_jacky = stockPost_jacky.trade(symbol_list.get(i), "buy", currentPrice, Double.toString(buy_num_jacky));

                            System.out.println(tradeResult);
                            System.out.println("jacky::"+tradeResult_jacky);
                            System.out.println(currentTime);

                            total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                            last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));


                            FileWrite.write("./buy_summary.txt", "buy_time:" + currentTime + "buy_price:" +
                                    currentPrice + "buy_coin_name:" + symbol_list.get(i) + "tradeResult:" + tradeResult + "tradeResult_jacky:" + tradeResult_jacky);

                            buy_time.put(symbol_list.get(i), (Long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(0));

                            Map<String, Double> tmp1 = new HashMap<>();
                            tmp1.put(symbol_list.get(i), Double.parseDouble(currentPrice));

                            Map<Long,Map<String, Double>> tmp2 = new HashMap<>();
                            tmp2.put(System.currentTimeMillis(), tmp1);

                            ORDER_NO_JACKY.put(JSONObject.parseObject(tradeResult_jacky).get("order_id").toString(), tmp2);

                            ORDER_NO.put(JSONObject.parseObject(tradeResult).get("order_id").toString(), tmp2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        buy_point.put(symbol_list.get(i), true);
                    }
                }
            }

            if (last_buy_price.get(symbol_list.get(i)) > 0D) {
//                if (((Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) > 0.012D)
//                        || ((Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) < -0.02D)) {
                if (((stoch1_now.doubleValue() - stoch1_last.doubleValue()) / 2D < -1.5D && stoch1_now.doubleValue() > 40
                        && k_now.doubleValue() > 40 ) || ((Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) < -0.07D)){
                    System.out.println("达到卖点：：：" + user_symbol_num + "time:" + currentTime);
                    if (Double.parseDouble(user_symbol_num) > 0.0001D) {
                        try {
                            String tradeResult = stockPost.trade(symbol_list.get(i), "sell", currentPrice, user_symbol_num);
                            System.out.println(tradeResult);
                            System.out.println(currentTime);
                            FileWrite.write("./sold_summary.txt", "sold_time:" + currentTime + "sold_price:" +
                                    currentPrice + "sold_coin_name:" + symbol_list.get(i) + "tradeResult:" + tradeResult);
                            last_buy_price.put(symbol_list.get(i), 0D);
                        } catch (HttpException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Double.parseDouble(jacky_symbol_num) > 0.0001D) {
                        try {
                            String tradeResult = stockPost_jacky.trade(symbol_list.get(i), "sell", currentPrice, jacky_symbol_num);
                            System.out.println(tradeResult);
                            System.out.println(currentTime);
                            FileWrite.write("./sold_summary.txt", "sold_time:" + currentTime + "sold_price:" +
                                    currentPrice + "sold_coin_name:" + symbol_list.get(i) + "jackytradeResult:" + tradeResult);
                            last_buy_price.put(symbol_list.get(i), 0D);
                        } catch (HttpException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

//    public static void main(String[] args) {
//        final FormalTrade t = new FormalTrade();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    t.trade();
//                } catch (Exception e) {
//                    System.out.println("报错啦");
//                    e.printStackTrace();
//                }
//            }
//        }, 1000, 30000);
//    }
}
