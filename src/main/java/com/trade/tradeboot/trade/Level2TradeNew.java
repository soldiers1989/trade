package com.trade.tradeboot.trade;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trade.tradeboot.dao.KlineDao;
import com.trade.tradeboot.entity.Kline;
import com.trade.tradeboot.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 模拟交易逻辑
 *
 * @author lizi
 */

@Service
public class Level2TradeNew {
    // 币对
    private List<String> symbolss = new ArrayList<>(Arrays.asList("btc_usdt", "eth_usdt", "bch_usdt", "eos_usdt", "etc_usdt", "ltc_usdt"));
//    private List<String> symbolss = new ArrayList<>(Arrays.asList(
//            "btc_usdt"
//    ));


    private Map<String, Long> buy_time = new ConcurrentHashMap<String, Long>() {
        {
            put("btc_usdt", 0L);
            put("eth_usdt", 0L);
            put("xrp_usdt", 0L);
            put("bch_usdt", 0L);
            put("eos_usdt", 0L);
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
    private Map<String, Boolean> buy_point = new ConcurrentHashMap<String, Boolean>() {
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


    private Map<String, Double> coin_num = new ConcurrentHashMap<String, Double>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt", 0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };


    private Map<String, Double> last_buy_price = new ConcurrentHashMap<String, Double>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt", 0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };


    private Map<String, Double> last_add_buy_price = new ConcurrentHashMap<String, Double>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt", 0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };

    private Map<String, Double> last_sold_price = new ConcurrentHashMap<String, Double>() {
        {
            put("btc_usdt", 0D);
            put("eth_usdt", 0D);
            put("xrp_usdt", 0D);
            put("bch_usdt", 0D);
            put("eos_usdt", 0D);
            put("ltc_usdt", 0D);
            put("xlm_usdt", 0D);
            put("trx_usdt", 0D);
            put("neo_usdt", 0D);
            put("xmr_usdt", 0D);
            put("etc_usdt", 0D);
        }
    };


    private Map<String, List<Map<String, Object>>> buy_summary = new ConcurrentHashMap<String, List<Map<String, Object>>>() {
        {
            put("btc_usdt", new ArrayList<Map<String, Object>>());
            put("eth_usdt", new ArrayList<Map<String, Object>>());
            put("xrp_usdt", new ArrayList<Map<String, Object>>());
            put("bch_usdt", new ArrayList<Map<String, Object>>());
            put("eos_usdt", new ArrayList<Map<String, Object>>());
            put("ltc_usdt", new ArrayList<Map<String, Object>>());
            put("xlm_usdt", new ArrayList<Map<String, Object>>());
            put("trx_usdt", new ArrayList<Map<String, Object>>());
            put("neo_usdt", new ArrayList<Map<String, Object>>());
            put("xmr_usdt", new ArrayList<Map<String, Object>>());
            put("etc_usdt", new ArrayList<Map<String, Object>>());
        }
    };

    private Map<String, List<Map<String, Object>>> add_buy_summary = new ConcurrentHashMap<String, List<Map<String, Object>>>() {
        {
            put("btc_usdt", new ArrayList<Map<String, Object>>());
            put("eth_usdt", new ArrayList<Map<String, Object>>());
            put("xrp_usdt", new ArrayList<Map<String, Object>>());
            put("bch_usdt", new ArrayList<Map<String, Object>>());
            put("eos_usdt", new ArrayList<Map<String, Object>>());
            put("ltc_usdt", new ArrayList<Map<String, Object>>());
            put("xlm_usdt", new ArrayList<Map<String, Object>>());
            put("trx_usdt", new ArrayList<Map<String, Object>>());
            put("neo_usdt", new ArrayList<Map<String, Object>>());
            put("xmr_usdt", new ArrayList<Map<String, Object>>());
            put("etc_usdt", new ArrayList<Map<String, Object>>());
        }
    };


    private Map<String, List<Map<String, Object>>> sold_summary = new ConcurrentHashMap<String, List<Map<String, Object>>>() {
        {
            put("btc_usdt", new ArrayList<Map<String, Object>>());
            put("eth_usdt", new ArrayList<Map<String, Object>>());
            put("xrp_usdt", new ArrayList<Map<String, Object>>());
            put("bch_usdt", new ArrayList<Map<String, Object>>());
            put("eos_usdt", new ArrayList<Map<String, Object>>());
            put("ltc_usdt", new ArrayList<Map<String, Object>>());
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
    private Double buy_percent = 0.08D;

    //补仓
    private Double add_buy_percent = 0.02D;


    // 总仓
    private volatile Double total_price = 100000D;

    // 手续费
    private Double rate = 0.0015D;

    // stochrsi最小值
    private Double min_stochRsi = 15D;

    // 高点交叉的stochRSI设置
    private int maxK_last = 70;

    private int maxK_now = 70;

    private int maxD_last = 70;

    private int maxD_now = 70;


    public void test() {
        for (int i = 0; i < 10; i++) {
            System.out.println(1);
        }
    }


    @Autowired
    private KlineDao klineDao;


    @PostConstruct
    public void start() {

        ExecutorService exec = Executors.newCachedThreadPool();

        for (final String param : symbolss) {
            try {
                Runnable run = () -> {
                    try {
                        List<String> t = new ArrayList<>();
                        t.add(param);
                        trade(t);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                exec.execute(run);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
        try {
            exec.awaitTermination(8, TimeUnit.HOURS);
            System.out.println("=============线程执行完毕=============");
            System.out.println(total_price);
            System.out.println(buy_summary);
            System.out.println(sold_summary);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void trade(List<String> symbols) {

        Map<String, JSONArray> newKline = new HashMap<>();

//        long start = 1530374400000L;
        long end = 1535731140000L;
        long start = 1530374400000L;

        JSONArray data_tmp = new JSONArray();

        for (String sym1 : symbols) {
            Map<String, String> res = HttpRequest.httpForGetRequest("https://www.okex.com/api/v1/kline.do?symbol=" + sym1 + "&type=" + type + "&since=" + (start - 100 * 2 * 60 * 60 * 1000L));
            JSONObject jo = JSONObject.parseObject(res.toString().replace("=", ":"));
            JSONArray tmp = jo.getJSONArray("data");
            System.out.println(tmp);
            newKline.put(sym1, tmp);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sd = sdf.format(new Date(start));
            String sd1 = sdf.format(new Date(end));
            List<Kline> l;
            List<Kline> l1;
            if (!sd.substring(5, 7).equals(sd1.substring(4, 6))) {
                l = klineDao.getLevel2(sym1.split("_")[0], sym1.split("_")[1], "2hour", start, MathCaclateUtil.getMonthEnd(new Date(start)), Integer.parseInt(sd.substring(5, 7)));
                l1 = klineDao.getLevel2(sym1.split("_")[0], sym1.split("_")[1], "2hour", MathCaclateUtil.getMonthBegin(new Date(end)), end, Integer.parseInt(sd1.substring(5, 7)));
                l.addAll(l1);
            } else {
                l = klineDao.getLevel2(sym1.split("_")[0], sym1.split("_")[1], "2hour", start, end, Integer.parseInt(sd.substring(5, 7)));
            }
//            List<Kline>  l = klineDao.getLevel2(sym1.split("_")[0], sym1.split("_")[1], "2hour", start, end, 8);
            JSONArray tt1 = new JSONArray();
            for (Kline k : l) {
                JSONArray tt = new JSONArray();
                tt.add(Long.parseLong(k.getReceiveTime()));
                tt.add(Double.parseDouble(k.getOpenPrice()));
                tt.add(Double.parseDouble(k.getHighPrice()));
                tt.add(Double.parseDouble(k.getLowPrice()));
                tt.add(Double.parseDouble(k.getClosePrice()));
                tt.add(Double.parseDouble(k.getVolume()));
                tt.add(tmp);
                tt1.add(tt);
            }
            data_tmp.add(tt1);
            System.out.println("==================数据库查询成功===================");
            //            data_tmp.add(l2.getLevel2(sym1.split("_")[0], sym1.split("_")[1], "2hour", start, end));
        }


        for (int xxx = 0; xxx < ((JSONArray) data_tmp.get(0)).size(); xxx++) {


            List<JSONArray> klines = new ArrayList<>();

            List<JSONArray> macd = new ArrayList<>();

            String s = "";

            List<JSONArray> kdj = new ArrayList<>();

            List<JSONArray> marsi = new ArrayList<>();

            List<String> symbol_list = new ArrayList<>();


            List<JSONArray> k = new ArrayList<>();

            List<JSONArray> d = new ArrayList<>();
            // 得到所有币种的k线数据
            for (String sym : symbols) {

                JSONArray replace = new JSONArray();
                JSONArray data2 = new JSONArray();
                data2.addAll(newKline.get(sym));


                List<Object> data = new ArrayList<>();
                for (int x = 0; x < data2.size(); x++) {

                    if ((long) ((JSONArray) data_tmp.getJSONArray(0).get(xxx)).get(0) - (long) data2.getJSONArray(x).get(0) >= 0 && (long) ((JSONArray) data_tmp.getJSONArray(0).get(xxx)).get(0) - (long) data2.getJSONArray(x).get(0) < 2 * 60 * 60 * 1000) {
                        // 说明这个点是最后一个点 即要替换掉的点
//                        data_tmp.getJSONArray(xxx).set(0, data2.getJSONArray(x).get(0));
                        replace = (JSONArray) data_tmp.getJSONArray(0).get(xxx);
                        data = data2.subList(0, x + 1);
                        break;
                    }
                }
                data.set(data.size() - 1, replace);

                macd.add(Macd.getMacd(JSONArray.parseArray(data.toString())));
                kdj.add(Kdj.getKdj(JSONArray.parseArray(data.toString())));
                marsi.add(Marsi.getMarsi(JSONArray.parseArray(data.toString())));
                klines.add(JSONArray.parseArray(data.toString()));
                symbol_list.add(sym);
                JSONArray rsi = Rsi.getRsi(JSONArray.parseArray(data.toString()));

                JSONObject param = new JSONObject();
                param.put("rsi", rsi.toString());
                param.put("n", "12");
                param.put("m", "12");
                param.put("p1", "3");
                param.put("p2", "3");
                param.put("kline", data);
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

            for (int i = 0; i < symbol_list.size(); i++) {


                long currentTime = (long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(0);
                String currentPrice = (((List<Object>) klines.get(i).get(klines.get(i).size() - 1)).get(4)).toString();
                if (klines.get(i).size() >= 2) {
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

                    if (currentTime - 1533304740000L == 0) {
                        System.out.println(currentTime);
                        System.out.println("测试");
                        System.out.println(currentPrice);
                        System.out.println(stoch1_last);
                        System.out.println(stoch2_last);
                        System.out.println(stoch1_now);
                        System.out.println(stoch2_now);
                        System.out.println(k_last);
                        System.out.println(k_now);
                        System.out.println(macd_now);
                        System.out.println(macd_last);
                        System.out.println(k.get(i));
                        System.out.println(d.get(i));
                    }


                    double[] var_now = new double[2];
                    double[] var_last = new double[2];
                    var_now[0] = k_now.doubleValue();
                    var_now[1] = d_now.doubleValue();

                    var_last[0] = k_last.doubleValue();
                    var_last[1] = d_last.doubleValue();


                    // 补仓机制
//                    if (MathCaclateUtil.divide(
//                            MathCaclateUtil.subtract(new BigDecimal(currentPrice).doubleValue(), last_add_buy_price.get(symbol_list.get(i)), BigDecimal.ROUND_HALF_UP),
//                            last_add_buy_price.get(symbol_list.get(i)) , BigDecimal.ROUND_HALF_UP) < -0.01D && coin_num.get(symbol_list.get(i)) > 0D)  {
//                        // 跌1% 补仓2%
//
//                        coin_num.put(symbol_list.get(i), MathCaclateUtil.add((Double) coin_num.get(symbol_list.get(i)), MathCaclateUtil.divide(MathCaclateUtil.divide(MathCaclateUtil.multiply(total_price, add_buy_percent, BigDecimal.ROUND_HALF_UP),
//                                last_buy_price.get(symbol_list.get(i)), BigDecimal.ROUND_HALF_UP),
//                                MathCaclateUtil.subtract(1D, rate,  BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));
//
//                        Map<String, Object> add_buy_map = new HashMap<>();
//
//                        total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, add_buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);
//                        add_buy_map.put("补仓时间：", MathCaclateUtil.formalDate(currentTime));
//                        add_buy_map.put("补仓后总仓：", total_price);
//                        add_buy_map.put("补仓时价格:", currentPrice);
//                        add_buy_map.put("补仓币种：", symbol_list.get(i));
//                        add_buy_map.put("补仓数量：", coin_num.get(symbol_list.get(i)));
//                        add_buy_summary.get(symbol_list.get(i)).add(add_buy_map);
//                        last_add_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));
//                    }

                    // 买入逻辑
                    Map<String, Object> buy_map = new HashMap<>();
                    Map<String, Object> sold_map = new HashMap<>();

                    if (buy_point.get(symbol_list.get(i)) && currentTime - buy_time.get(symbol_list.get(i)) > 2 * 60 * 60 * 1000L) {
                        if (stoch1_now.compareTo(new BigDecimal(min_stochRsi)) == 1) {
                            // 达到买入条件
                            System.out.println(currentTime);
                            System.out.println("=========1");
                            System.out.println(stoch1_last);
                            System.out.println(stoch2_last);
                            System.out.println(stoch1_now);
                            System.out.println(stoch2_now);
                            System.out.println(k_last);
                            System.out.println(k_now);
                            System.out.println(macd_now);
                            System.out.println(macd_last);
                            System.out.println(k.get(i));
                            System.out.println(d.get(i));


                            synchronized (this) {
                                // 买入数量 = （1 - 手续费）x（当前总仓 x 买入百分比）
                                coin_num.put(symbol_list.get(i), MathCaclateUtil.add((Double) coin_num.get(symbol_list.get(i)), MathCaclateUtil.divide(MathCaclateUtil.divide(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP),
                                        Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP),
                                        MathCaclateUtil.subtract(1D, rate, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));

                                buy_map.put("买入前总仓:", total_price);
                                total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                                last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));


                                buy_map.put("买入时间：", MathCaclateUtil.formalDate(currentTime));
                                buy_map.put("买入后总仓：", total_price);
                                buy_map.put("买入价格:", currentPrice);
                                buy_map.put("买入币种：", symbol_list.get(i));
                                sold_map.put("买入数量：", coin_num.get(symbol_list.get(i)));

                                last_add_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));

                                buy_summary.get(symbol_list.get(i)).add(buy_map);
                                buy_point.put(symbol_list.get(i), false);
                                buy_time.put(symbol_list.get(i), (long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 2)).get(0) + 2 * 60 * 60 * 1000L);
                            }
                        }
                    } else if (macd_now.compareTo(macd_last) == 1 && stoch1_last.compareTo(stoch2_last) < 0 && stoch1_now.compareTo(stoch2_now) >= 0
                            && stoch1_last.compareTo(new BigDecimal(maxK_last)) == -1 && stoch2_last.compareTo(new BigDecimal(maxD_last)) == -1
                            && stoch1_now.compareTo(new BigDecimal(maxK_now)) == -1 && stoch1_now.compareTo(new BigDecimal(maxD_now)) == -1
                            && currentTime - buy_time.get(symbol_list.get(i)) > 2 * 60 * 60 * 1000L) {
//                        if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
//                                && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
//                        if ((new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
//                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1)) {
                        if ((k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == 1
                                && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == 1)
                                || (k_now.compareTo(d_now) == 1 && k_last.compareTo(d_last) == -1) || (k_now.compareTo(d_now) == -1
                                && k_last.compareTo(d_last) == -1 && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) == -1)) {
                            if (stoch1_now.compareTo(new BigDecimal(min_stochRsi)) == 1) {
                                // 达到买入条件
                                System.out.println(currentTime);
                                System.out.println("======= ==2");
                                System.out.println(stoch1_last);
                                System.out.println(stoch2_last);
                                System.out.println(stoch1_now);
                                System.out.println(stoch2_now);
                                System.out.println(k_last);
                                System.out.println(k_now);
                                System.out.println(macd_now);
                                System.out.println(macd_last);
                                System.out.println(k.get(i));
                                System.out.println(d.get(i));

                                synchronized (this) {
                                    // 买入数量 = （1 - 手续费）x（当前总仓 x 买入百分比）
                                    coin_num.put(symbol_list.get(i), MathCaclateUtil.add((Double) coin_num.get(symbol_list.get(i)), MathCaclateUtil.divide(MathCaclateUtil.divide(MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP),
                                            Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP),
                                            MathCaclateUtil.subtract(1D, rate, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP));

                                    buy_map.put("买入前总仓:", total_price);

                                    total_price = MathCaclateUtil.subtract(total_price, MathCaclateUtil.multiply(total_price, buy_percent, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);

                                    last_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));


                                    buy_map.put("买入时间：", MathCaclateUtil.formalDate(currentTime));
                                    buy_map.put("买入后总仓：", total_price);
                                    buy_map.put("买入价格:", currentPrice);
                                    buy_map.put("买入币种：", symbol_list.get(i));
                                    sold_map.put("买入数量：", coin_num.get(symbol_list.get(i)));

                                    buy_summary.get(symbol_list.get(i)).add(buy_map);

                                    last_add_buy_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));

                                    buy_time.put(symbol_list.get(i), (long) ((List<Object>) klines.get(i).get(klines.get(i).size() - 2)).get(0) + 2 * 60 * 60 * 1000L);
                                }
                            } else {
                                synchronized (this) {
                                    buy_point.put(symbol_list.get(i), true);
                                }
                            }
                        }
                    }


                    // 卖点

//                    if (coin_num.get(symbol_list.get(i)) > 0D) {
//                        if (j_now.compareTo(new BigDecimal(80)) >= 0 && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) < 0) {
//                            System.out.println(currentTime);
//                            System.out.println("=========3");
//                            System.out.println(stoch1_last);
//                            System.out.println(stoch2_last);
//                            System.out.println(stoch1_now);
//                            System.out.println(stoch2_now);
//                            System.out.println(k_last);
//                            System.out.println(k_now);
//                            System.out.println(macd_now);
//                            System.out.println(macd_last);
//                            System.out.println(k.get(i));
//                            System.out.println(d.get(i));
//                            synchronized (this) {
//                                buy_map.put("卖出前总仓:", total_price);
//                                total_price = MathCaclateUtil.add(total_price, MathCaclateUtil.multiply(coin_num.get(symbol_list.get(i)), Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);
//                                sold_map.put("卖出时间:", MathCaclateUtil.formalDate(currentTime));
//                                sold_map.put("卖出价格:", currentPrice);
//                                sold_map.put("卖出后总仓:", total_price);
//                                sold_map.put("卖出币种:", symbol_list.get(i));
//                                sold_map.put("卖出数量：", coin_num.get(symbol_list.get(i)));
//                                sold_summary.get(symbol_list.get(i)).add(sold_map);
//                                last_sold_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));
//                                coin_num.put(symbol_list.get(i), 0D);
//                            }
//                        } else if (MathCaclateUtil.divide(k_now.doubleValue(), MathCaclateUtil.add(k_last.doubleValue(), 0.01D, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP) < 1D) {
//                            if ((k_last.compareTo(d_last) > 0 && k_now.compareTo(d_now) > 0 && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) < 0)
//                                    || (k_last.compareTo(d_last) < 0 && k_now.compareTo(d_now) < 0 && new BigDecimal(MathCaclateUtil.Variance(var_now)).compareTo(new BigDecimal(MathCaclateUtil.Variance(var_last))) > 0)
//                                    || (k_last.compareTo(d_last) > 0 && k_now.compareTo(d_now) < 0)) {
//                                System.out.println(currentTime);
//                                System.out.println("=========4");
//                                System.out.println(stoch1_last);
//                                System.out.println(stoch2_last);
//                                System.out.println(stoch1_now);
//                                System.out.println(stoch2_now);
//                                System.out.println(k_last);
//                                System.out.println(k_now);
//                                System.out.println(macd_now);
//                                System.out.println(macd_last);
//                                System.out.println(k.get(i));
//                                System.out.println(d.get(i));
//
//                                synchronized (this) {
//                                    buy_map.put("卖出前总仓:", total_price);
//                                    total_price = MathCaclateUtil.add(total_price, MathCaclateUtil.multiply(coin_num.get(symbol_list.get(i)), Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);
//                                    sold_map.put("卖出时间:", MathCaclateUtil.formalDate(currentTime));
//                                    sold_map.put("卖出价格:", currentPrice);
//                                    sold_map.put("卖出后总仓:", total_price);
//                                    sold_map.put("卖出币种:", symbol_list.get(i));
//                                    sold_map.put("卖出数量：", coin_num.get(symbol_list.get(i)));
//                                    sold_summary.get(symbol_list.get(i)).add(sold_map);
//                                    last_sold_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));
//                                    coin_num.put(symbol_list.get(i), 0D);
//                                }
//                            }
//                        }
//                    }
//                    if ((coin_num.get(symbol_list.get(i)) > 0D && (Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) > 0.012D)
//                        || (coin_num.get(symbol_list.get(i)) > 0D && (Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) < -0.02D)) {
//                    if ((coin_num.get(symbol_list.get(i)) > 0D && stoch1_now.doubleValue() / stoch1_last.doubleValue() < 0.92D && stoch1_now.doubleValue() > 50 && stoch1_now.doubleValue() < stoch1_last.doubleValue())){
//                    if ((coin_num.get(symbol_list.get(i)) > 0D && stoch1_now.doubleValue() - stoch1_last.doubleValue() < -6D && stoch1_now.doubleValue() > 50)){
//                    if ((coin_num.get(symbol_list.get(i)) > 0D && (stoch1_now.doubleValue() - stoch1_last.doubleValue()) / 2D < -1.5D && stoch1_now.doubleValue() > 50
//                     && k_now.doubleValue() > 50 ) || (coin_num.get(symbol_list.get(i)) > 0D && (Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) < -0.04D)){
                    if ((coin_num.get(symbol_list.get(i)) > 0D && (stoch1_now.doubleValue() - stoch1_last.doubleValue()) / 2D < -1.5D && stoch1_now.doubleValue() > 50
                            && k_now.doubleValue() > 50) || (coin_num.get(symbol_list.get(i)) > 0D && (Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i))) / last_buy_price.get(symbol_list.get(i)) < -0.04D)) {
//                    if ((coin_num.get(symbol_list.get(i)) > 0D && (stoch1_now.doubleValue() - stoch1_last.doubleValue()) / 2D < -1.5D && stoch1_now.doubleValue() > 20
//                            && k_now.doubleValue() > 30 ) || (coin_num.get(symbol_list.get(i)) > 0D && (Double.parseDouble(currentPrice) - last_buy_price.get(symbol_list.get(i)) )/ last_buy_price.get(symbol_list.get(i)) < -0.04D)){
                        synchronized (this) {
                            buy_map.put("卖出前总仓:", total_price);
                            total_price = MathCaclateUtil.add(total_price, MathCaclateUtil.multiply(coin_num.get(symbol_list.get(i)), Double.parseDouble(currentPrice), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);
                            sold_map.put("卖出时间:", MathCaclateUtil.formalDate(currentTime));
                            sold_map.put("卖出价格:", currentPrice);
                            sold_map.put("卖出后总仓:", total_price);
                            sold_map.put("卖出币种:", symbol_list.get(i));
                            sold_map.put("卖出数量：", coin_num.get(symbol_list.get(i)));
                            sold_summary.get(symbol_list.get(i)).add(sold_map);
                            last_sold_price.put(symbol_list.get(i), Double.parseDouble(currentPrice));
                            coin_num.put(symbol_list.get(i), 0D);
                        }
                    }
                }
            }
        }

        FileWrite.write("./buy.txt", buy_summary.get(symbols.get(0)).toString());
        FileWrite.write("./sold.txt", sold_summary.get(symbols.get(0)).toString());
        System.out.println(buy_summary);
        System.out.println(sold_summary);
        System.out.println(add_buy_summary);
        System.out.println(coin_num);
        synchronized (this) {
            for (Map.Entry<String, Double> entry : coin_num.entrySet()) {
                //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                //entry.getKey() ;entry.getValue(); entry.setValue();
                //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
                System.out.println("key= " + entry.getKey() + " and value= "
                        + entry.getValue());
                if (entry.getValue() > 0D) {
                    total_price = MathCaclateUtil.add(total_price, MathCaclateUtil.multiply(entry.getValue(), last_sold_price.get(entry.getKey()), BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP);
                    coin_num.put(entry.getKey(), 0D);
                }
            }
        }
        System.out.println(total_price);
    }

//    public static void main(String[] args) {
////        try {
////            Level2Trade t = new Level2Trade();
////            t.trade(t.symbolss);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        final Level2Trade t = new Level2Trade();
//        for (final String param: t.symbolss) {
//            try {
//                Thread t1 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<String> tmp = new ArrayList<>();
//                        tmp.add(param);
//                        t.trade(tmp);
//                    }
//                });
//                t1.sleep(500);
//                t1.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

}
