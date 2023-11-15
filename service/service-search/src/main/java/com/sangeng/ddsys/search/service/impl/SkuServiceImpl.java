package com.sangeng.ddsys.search.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sangeng.ddsys.activity.ActivityFeignClient;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.common.auth.AuthContextHolder;
import com.sangeng.ddsys.enums.SkuType;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.model.search.SkuEs;
import com.sangeng.ddsys.search.repository.SkuRepository;
import com.sangeng.ddsys.search.service.SkuService;
import com.sangeng.ddsys.vo.search.SkuEsQueryVo;

/**
 * @author: calos
 * @create: 2023-11-05 12:56
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuEsRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Resource
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void upperSku(Long skuId) {
        // 1、通过远程调用，根据skuid获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        // 2、获取数据封装SkuEs对象
        SkuEs skuEs = new SkuEs();
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (Objects.equals(skuInfo.getSkuType(), SkuType.COMMON.getCode())) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            // TODO 待完善-秒杀商品

        }
        // 3、调用方法添加ES
        SkuEs save = skuEsRepository.save(skuEs);
    }

    @Override
    public void lowerSku(Long skuId) {
        skuEsRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHotSkuList() {
        // find read get开头
        // 关联条件关键词
        // 0表示第一页
        Pageable pageable = PageRequest.of(0, 10);
        return skuEsRepository.findByOrderByHotScoreDesc(pageable).getContent();
    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        Page<SkuEs> page;
        if (StringUtils.isEmpty(skuEsQueryVo.getKeyword())) {
            page = skuEsRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(), skuEsQueryVo.getWareId(),
                pageable);
        } else {
            page =
                skuEsRepository.findByKeywordAndWareId(skuEsQueryVo.getKeyword(), skuEsQueryVo.getWareId(), pageable);
        }

        List<SkuEs> skuEsList = page.getContent();
        // 获取sku对应的促销活动标签
        if (!CollectionUtils.isEmpty(skuEsList)) {
            List<Long> skuIdList = skuEsList.stream().map(SkuEs::getId).collect(Collectors.toList());
            Map<Long, List<String>> skuIdToRuleListMap = activityFeignClient.findActivity(skuIdList);
            if (null != skuIdToRuleListMap) {
                skuEsList.forEach(skuEs -> skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId())));
            }
        }
        return page;
    }

    @Override
    public void incrHotScore(Long skuId) {
        // 定义key
        String hotKey = "hotScore";
        // 保存数据
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);
        if (hotScore % 10 == 0) {
            // 更新es
            Optional<SkuEs> optional = skuEsRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuEsRepository.save(skuEs);
        }
    }
}
