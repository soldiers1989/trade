package com.trade.tradeboot.dao;


import com.trade.tradeboot.entity.Kline;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * create by lizi
 */
public interface KlineDao {

	List<Kline> getLevel2(@Param("s") String s, @Param("s1") String s1, @Param("s2") String s2, @Param("start") long start, @Param("end") long end, @Param("month") int month);

}
