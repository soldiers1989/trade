package com.trade.tradeboot.trade;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.*;
import com.trade.tradeboot.util.*;


/**
 * 模拟交易逻辑
 * @author lizi
 */
public class Trade {
    // 币对
//    private List<String> symbols = new ArrayList<>(Arrays.asList("btc_usdt", "eth_usdt", "xrp_usdt", "bch_usdt", "eos_usdt", "ltc_usdt", "xlm_usdt", "trx_usdt","neo_usdt", "xmr_usdt","etc_usdt"));
    private List<String> symbols = new ArrayList<>(Arrays.asList(
            "btc_usdt"
    ));

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


    private Map<String, Object> coin_num = new HashMap<String, Object>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt",0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };


    private Map<String, Object> last_buy_price = new HashMap<String, Object>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt",0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };




    private Map<String, List<Map<String, Object>>> buy_summary = new HashMap<String, List<Map<String, Object>>>() {
        {
            put("btc_usdt", new ArrayList<Map<String, Object>>());
            put("eth_usdt", new ArrayList<Map<String, Object>>());
            put("xrp_usdt", new ArrayList<Map<String, Object>>());
            put("bch_usdt", new ArrayList<Map<String, Object>>());
            put("eos_usdt", new ArrayList<Map<String, Object>>());
            put("ltc_usdt",new ArrayList<Map<String, Object>>());
            put("xlm_usdt", new ArrayList<Map<String, Object>>());
            put("trx_usdt", new ArrayList<Map<String, Object>>());
            put("neo_usdt", new ArrayList<Map<String, Object>>());
            put("xmr_usdt", new ArrayList<Map<String, Object>>());
            put("etc_usdt", new ArrayList<Map<String, Object>>());
        }
    };


    private Map<String, List<Map<String, Object>>> sold_summary = new HashMap<String, List<Map<String, Object>>>() {
        {
            put("btc_usdt", new ArrayList<Map<String, Object>>());
            put("eth_usdt", new ArrayList<Map<String, Object>>());
            put("xrp_usdt", new ArrayList<Map<String, Object>>());
            put("bch_usdt", new ArrayList<Map<String, Object>>());
            put("eos_usdt", new ArrayList<Map<String, Object>>());
            put("ltc_usdt",new ArrayList<Map<String, Object>>());
            put("xlm_usdt", new ArrayList<Map<String, Object>>());
            put("trx_usdt", new ArrayList<Map<String, Object>>());
            put("neo_usdt", new ArrayList<Map<String, Object>>());
            put("xmr_usdt", new ArrayList<Map<String, Object>>());
            put("etc_usdt", new ArrayList<Map<String, Object>>());
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
    private Double buy_percent = 0.2D;

    // 总仓
    private Double total_price = 100000D;

    // 手续费
    private Double rate = 0.0015D;

    // 高点交叉的stochRSI设置
    private int maxK_last = 70;

    private int maxK_now = 70;

    private int maxD_last = 70;

    private int maxD_now = 70;




    public void trade() {

        List<JSONArray> klines = new ArrayList<>();

        List<JSONArray> macd = new ArrayList<>();

        List<JSONArray> kdj = new ArrayList<>();

        List<JSONArray> marsi = new ArrayList<>();

        List<String> symbol_list = new ArrayList<>();


        List<JSONArray> k = new ArrayList<>();

        List<JSONArray> d = new ArrayList<>();


        // 得到所有币种的k线数据
        for (String sym: symbols) {
            Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol="+ sym +"&type=" + type + "&size=700");
            JSONObject jo = JSONObject.parseObject(res.toString().replace("=",":"));
            JSONArray data = jo.getJSONArray("data");
//            Level2DataUtil l2 = new Level2DataUtil();
//            JSONArray data = l2.getLevel2(sym.split("_")[0],sym.split("_")[1],"2hour", 1531299531000L, 1531299828000L);
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

            Map<String, String> res1 = HttpRequest.httpForPostRequest("https://wechatpublic.pgrab.cn/getStochRSI", param);
            JSONObject jo1 = JSONObject.parseObject(res1.toString().replace("=",":"));
            JSONObject jo2 = jo1.getJSONObject("data").getJSONObject("data");
            System.out.println(jo2);
            JSONArray k_tmp = jo2.getJSONArray("k");
            JSONArray d_tmp = jo2.getJSONArray("d");
            k.add(k_tmp);
            d.add(d_tmp);
        }

        for (int i=0; i<symbol_list.size(); i++) {

            for (int j=0; j<klines.get(i).size();j++) {
                Map<String, Object> buy_map = new HashMap<>();

                long currentTime = (long)((List<Object>) klines.get(i).get(j)).get(0);
                String currentPrice = ((BigDecimal)((List<Object>) klines.get(i).get(j)).get(4)).toString();
                if (j >= 2) {
                    BigDecimal macd_now = (BigDecimal) ((List<Object>) macd.get(i).get(j-1)).get(1);
                    BigDecimal macd_last = (BigDecimal) ((List<Object>) macd.get(i).get(j-2)).get(1);
                    BigDecimal k_now = (BigDecimal) ((List<Object>) kdj.get(i).get(j-1)).get(1);
                    BigDecimal d_now = (BigDecimal) ((List<Object>) kdj.get(i).get(j-1)).get(2);
                    BigDecimal j_now = (BigDecimal) ((List<Object>) kdj.get(i).get(j-1)).get(3);
                    BigDecimal k_last = (BigDecimal) ((List<Object>) kdj.get(i).get(j-2)).get(1);
                    BigDecimal d_last = (BigDecimal) ((List<Object>) kdj.get(i).get(j-2)).get(2);
                    BigDecimal j_last = (BigDecimal) ((List<Object>) kdj.get(i).get(j-2)).get(3);
                    BigDecimal marsi1_now = (BigDecimal) ((List<Object>) marsi.get(i).get(j-1)).get(1);
                    BigDecimal marsi2_now = (BigDecimal) ((List<Object>) marsi.get(i).get(j-1)).get(2);
                    BigDecimal marsi1_last = (BigDecimal) ((List<Object>) marsi.get(i).get(j-2)).get(1);
                    BigDecimal marsi2_last = (BigDecimal) ((List<Object>) marsi.get(i).get(j-2)).get(2);
                    System.out.println(k.get(i).get(j-1));
                    BigDecimal stoch1_now = (BigDecimal) ((List<Object>) k.get(i).get(j-1)).get(1);
                    BigDecimal stoch2_now = (BigDecimal) ((List<Object>) d.get(i).get(j-1)).get(1);
                    BigDecimal stoch1_last = (BigDecimal) ((List<Object>) k.get(i).get(j-2)).get(1);
                    BigDecimal stoch2_last = (BigDecimal) ((List<Object>) d.get(i).get(j-2)).get(1);

                    double[] var_now = new double[2];
                    double[] var_last = new double[2];
                    var_now[0] = k_now.doubleValue();
                    var_now[1] = d_now.doubleValue();

                    var_last[0] = k_last.doubleValue();
                    var_last[1] = d_last.doubleValue();

                    if (currentTime == 1528322400000L) {
                        System.out.println("====xxxxxxx");
                        System.out.println(stoch1_last);
                        System.out.println(stoch2_last);
                        System.out.println(stoch1_now);
                        System.out.println(stoch2_now);
                        System.out.println(k_last);
                        System.out.println(k_now);
                        System.out.println(d_last);
                        System.out.println(d_now);
                        System.out.println(macd_now);
                        System.out.println(macd_last);
                        System.out.println(new BigDecimal(MathCaclateUtil.Variance(var_now)));
                        System.out.println(new BigDecimal(MathCaclateUtil.Variance(var_last)));
                    }

                    // 买入逻辑

                    if (buy_point.get(symbol_list.get(i))) {
                        if (stoch1_now.compareTo(new BigDecimal(10)) == 1) {
                            // 达到买入条件
                            System.out.println(currentTime);
                            System.out.println("=========");
                            System.out.println(stoch1_last);
                            System.out.println(stoch2_last);
                            System.out.println(stoch1_now);
                            System.out.println(stoch2_now);
                            System.out.println(k_last);
                            System.out.println(k_now);
                            System.out.println(macd_now);
                            System.out.println(macd_last);

                            // 买入数量 = （1 - 手续费）x（当前总仓 x 买入百分比）
                            coin_num.put(symbol_list.get(i), MathCaclateUtil.add((Double) coin_num.get(symbol_list.get(i)), MathCaclateUtil.divide(MathCaclateUtil.divide(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP),
                                    Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP),
                                    MathCaclateUtil.subtract(1D, rate,  BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));

                            System.out.println(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP));

                            total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                            last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));


                            buy_map.put("买入时间：", MathCaclateUtil.formalDate(currentTime));
                            buy_map.put("买入后总仓：", total_price);
                            buy_map.put("买入价格:", currentPrice);
                            buy_map.put("买入币种：", symbol_list.get(i));

                            buy_summary.get(symbol_list.get(i)).add(buy_map);
                            buy_point.put(symbol_list.get(i), false);
                        }
                    } else if (macd_now.compareTo(macd_last) == 1 && stoch1_last.compareTo(stoch2_last) <= 0 && stoch1_now.compareTo(stoch2_now) == 1
                            && stoch1_last.compareTo(new BigDecimal(maxK_last)) == -1 && stoch2_last.compareTo(new BigDecimal(maxD_last)) == -1
                            && stoch1_now.compareTo(new BigDecimal(maxK_now)) == -1 && stoch1_now.compareTo(new BigDecimal(maxD_now)) == -1) {
//                        if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
//                                && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
//                        if ((new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
                        if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
                                && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1) || (k_now.compareTo(d_now) == -1
                                && k_last.compareTo(d_last) == -1 &&  new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == -1)) {
                            if (stoch1_now.compareTo(new BigDecimal(10)) == 1) {
                                // 达到买入条件
                                System.out.println(currentTime);
                                System.out.println("=========");
                                System.out.println(stoch1_last);
                                System.out.println(stoch2_last);
                                System.out.println(stoch1_now);
                                System.out.println(stoch2_now);
                                System.out.println(k_last);
                                System.out.println(k_now);
                                System.out.println(macd_now);
                                System.out.println(macd_last);

                                // 买入数量 = （1 - 手续费）x（当前总仓 x 买入百分比）
                                coin_num.put(symbol_list.get(i), MathCaclateUtil.add((Double) coin_num.get(symbol_list.get(i)), MathCaclateUtil.divide(MathCaclateUtil.divide(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP),
                                        Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP),
                                        MathCaclateUtil.subtract(1D, rate,  BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));

                                System.out.println(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP));

                                total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                                last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));


                                buy_map.put("买入时间：", MathCaclateUtil.formalDate(currentTime));
                                buy_map.put("买入后总仓：", total_price);
                                buy_map.put("买入价格:", currentPrice);
                                buy_map.put("买入币种：", symbol_list.get(i));

                                buy_summary.get(symbol_list.get(i)).add(buy_map);
                            } else {
                                buy_point.put(symbol_list.get(i), true);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(buy_summary);
        System.out.println(coin_num);
    }

    public static void main(String[] args) {
        Trade t = new Trade();
        t.trade();
//
//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("1111");
//            }
//        });
//
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("2222");
//            }
//        });
//
//        t1.start();
//        t2.start();
//        Level2DataUtil l2 = new Level2DataUtil();
//        System.out.println(l2.getLevel2("btc","usdt","2hour", 1531299534000L, 1531299831000L).size());
    }

}
