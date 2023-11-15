package com.sangeng.ddsys.home.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.client.search.SearchFeignClient;
import com.sangeng.ddsys.client.user.UserFeignClient;
import com.sangeng.ddsys.home.service.HomeService;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.model.search.SkuEs;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:21
 **/
@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SearchFeignClient searchFeignClient;

    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public Map<String, Object> home(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取分类信息
        List<Category> categoryList = productFeignClient.findAllCategoryList();
        result.put("categoryList", categoryList);

        // 获取新人专享商品
        List<SkuInfo> newPersonSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        result.put("newPersonSkuInfoList", newPersonSkuInfoList);

        // TODO 获取用户首页秒杀数据

        // 提货点地址信息
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        result.put("leaderAddressVo", leaderAddressVo);

        // 获取爆品商品
        List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
        result.put("hotSkuList", hotSkuList);
        return result;
    }
}
