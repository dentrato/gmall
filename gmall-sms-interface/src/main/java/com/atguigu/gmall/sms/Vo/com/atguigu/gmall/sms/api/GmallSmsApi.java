package com.atguigu.gmall.sms.Vo.com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.Vo.SkuSaveSale;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("sms-service")
public interface GmallSmsApi {
    @PostMapping("sms/skubounds/sku/save/sale")
    public Resp<Object> savesale(@RequestBody SkuSaveSale skuSaveSale);
}