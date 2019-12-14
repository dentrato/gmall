package com.atguigu.gmall.sms.Vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuSaveSale {
    private Long skuId;

    //积分相关的字段
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    //打折相关的字段
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    //满减相关的字段
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
