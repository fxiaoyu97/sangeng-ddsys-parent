package com.sangeng.ddsys.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.activity.mapper.CouponInfoMapper;
import com.sangeng.ddsys.activity.mapper.CouponRangeMapper;
import com.sangeng.ddsys.activity.service.CouponInfoService;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.enums.CouponRangeType;
import com.sangeng.ddsys.model.activity.CouponInfo;
import com.sangeng.ddsys.model.activity.CouponRange;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam) {
        //  构造排序条件
        QueryWrapper<CouponInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        IPage<CouponInfo> page = baseMapper.selectPage(pageParam, queryWrapper);
        page.getRecords().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            if (null != item.getRangeType()) {
                item.setRangeTypeString(item.getRangeType().getComment());
            }
        });
        //  返回数据集合
        return page;
    }

    @Override
    public CouponInfo getCouponInfo(String id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        // 1、根据优惠券id查询优惠券基本信息 coupon_info表
        Map<String, Object> result = new HashMap<>();
        CouponInfo couponInfo = baseMapper.selectById(id);
        // 2、根据优惠券id查询coupon_range 查询里面对应的range_id
        // 如果规则类型sku range_id就是skuId
        // 如果规则类型是分类Category range_id就是分类id
        List<CouponRange> couponRangeList =
            couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));
        List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rangeIdList)) {
            return result;
        }

        // 3、分别判断封装不同数据
        if (couponInfo.getRangeType() == CouponRangeType.SKU) {
            // 如果规则类型是sku，得到skuId，远程调用根据多个skuId获取对应sku信息
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
            result.put("skuInfoList", skuInfoList);
        } else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
            // 如果规则类型是sku，得到分类id，远程调用根据分类id值，获得对应分类信息}
            List<Category> categoryList = productFeignClient.findCategoryList(rangeIdList);
            result.put("categoryList", categoryList);
        }
        return result;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
          /*
        优惠券couponInfo 与 couponRange 要一起操作：先删除couponRange ，更新couponInfo ，再新增couponRange ！
         */
        QueryWrapper<CouponRange> couponRangeQueryWrapper = new QueryWrapper<>();
        couponRangeQueryWrapper.eq("coupon_id", couponRuleVo.getCouponId());
        couponRangeMapper.delete(couponRangeQueryWrapper);

        //  更新数据
        CouponInfo couponInfo = this.getById(couponRuleVo.getCouponId());
        // couponInfo.setCouponType();
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);

        //  插入优惠券的规则 couponRangeList
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange : couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            //  插入数据
            couponRangeMapper.insert(couponRange);
        }
    }

    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {
        //  模糊查询
        QueryWrapper<CouponInfo> couponInfoQueryWrapper = new QueryWrapper<>();
        couponInfoQueryWrapper.like("coupon_name", keyword);
        return baseMapper.selectList(couponInfoQueryWrapper);
    }
}
