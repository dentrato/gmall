package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.Vo.com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SaveSale extends GmallSmsApi {

}
