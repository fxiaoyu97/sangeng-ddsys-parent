package com.sangeng.ddsys.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.common.constant.RedisConst;
import com.sangeng.ddsys.common.exception.DdsysException;
import com.sangeng.ddsys.common.result.ResultCodeEnum;
import com.sangeng.ddsys.model.product.SkuAttrValue;
import com.sangeng.ddsys.model.product.SkuImage;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.model.product.SkuPoster;
import com.sangeng.ddsys.mq.constant.MqConst;
import com.sangeng.ddsys.mq.service.RabbitService;
import com.sangeng.ddsys.product.mapper.SkuInfoMapper;
import com.sangeng.ddsys.product.service.SkuAttrValueService;
import com.sangeng.ddsys.product.service.SkuImageService;
import com.sangeng.ddsys.product.service.SkuInfoService;
import com.sangeng.ddsys.product.service.SkuPosterService;
import com.sangeng.ddsys.vo.product.SkuInfoQueryVo;
import com.sangeng.ddsys.vo.product.SkuInfoVo;
import com.sangeng.ddsys.vo.product.SkuStockLockVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    @Autowired
    private SkuPosterService skuPosterService;

    @Autowired
    private SkuImageService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        // 获取条件值
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        // 封装条件
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            wrapper.like(SkuInfo::getSkuName, keyword);
        }
        if (!StringUtils.isEmpty(skuType)) {
            wrapper.eq(SkuInfo::getSkuType, skuType);
        }
        if (!StringUtils.isEmpty(categoryId)) {
            wrapper.eq(SkuInfo::getCategoryId, categoryId);
        }
        // 调用方法查询
        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        // 1、保存sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.insert(skuInfo);
        // 2、保存sku海报
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            skuPosterList.forEach(skuPoster -> skuPoster.setSkuId(skuInfo.getId()));
            skuPosterService.saveBatch(skuPosterList);
        }
        // 3、保存sku的图片
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            skuImagesList.forEach(skuImage -> skuImage.setSkuId(skuInfo.getId()));
            skuImagesService.saveBatch(skuImagesList);
        }
        // 4、保存sku的平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            skuAttrValueList.forEach(skuAttrValue -> skuAttrValue.setSkuId(skuInfo.getId()));
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    @Override
    public SkuInfoVo getSkuInfoVo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        // 根据id查询sku基本信息
        SkuInfo skuInfo = baseMapper.selectById(id);
        // 根据di查询商品图片列表
        List<SkuImage> skuImageList = skuImagesService.findBySkuId(id);

        // 根据id查询商品海报列表
        List<SkuPoster> skuPosterList = skuPosterService.findBySkuId(id);

        // 根据id 查询商品属性信息列表
        List<SkuAttrValue> skuAttrValueList = skuAttrValueService.findBySkuId(id);

        // 封装所所有数据，返回
        BeanUtils.copyProperties(skuInfo, skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageList);
        skuInfoVo.setSkuPosterList(skuPosterList);
        skuInfoVo.setSkuAttrValueList(skuAttrValueList);
        return skuInfoVo;
    }

    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        Long id = skuInfoVo.getId();
        // 更新sku信息
        this.updateById(skuInfoVo);
        // 海报信息
        skuPosterService.remove(new LambdaQueryWrapper<SkuPoster>().eq(SkuPoster::getSkuId, id));
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            int sort = 1;
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(id);
                sort++;
            }
            skuPosterService.saveBatch(skuPosterList);
        }

        // 商品图片
        skuImagesService.remove(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, id));
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            int sort = 1;
            for (SkuImage skuImages : skuImagesList) {
                skuImages.setSkuId(id);
                skuImages.setSort(sort);
                sort++;
            }
            skuImagesService.saveBatch(skuImagesList);
        }

        // sku平台属性
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId, id));
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            int sort = 1;
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(id);
                skuAttrValue.setSort(sort);
                sort++;
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    // 商品审核
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void check(Long skuId, Integer status) {
        // 更改发布状态
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setCheckStatus(status);
        baseMapper.updateById(skuInfoUp);
    }

    @Override
    public void publish(Long skuId, Integer status) {
        // 更改发布状态
        if (status == 1) {
            SkuInfo skuInfoUp = new SkuInfo();
            skuInfoUp.setId(skuId);
            skuInfoUp.setPublishStatus(1);
            baseMapper.updateById(skuInfoUp);
            // 商品上架 发送mq消息更新es数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_UPPER, skuId);
        } else {
            SkuInfo skuInfoUp = new SkuInfo();
            skuInfoUp.setId(skuId);
            skuInfoUp.setPublishStatus(0);
            baseMapper.updateById(skuInfoUp);
            // 商品下架 发送mq消息更新es数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_LOWER, skuId);
        }
    }

    @Override
    public void isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsNewPerson(status);
        baseMapper.updateById(skuInfoUp);
    }

    // 批量获取sku信息
    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIdList) {
        return this.listByIds(skuIdList);
    }

    // 根据关键字获取sku列表
    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SkuInfo::getSkuName, keyword);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SkuInfo> findNewPersonList() {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        Page<SkuInfo> page = new Page<>(1, 3);
        wrapper.eq(SkuInfo::getIsNewPerson, 1).eq(SkuInfo::getPublishStatus, 1).orderByDesc(SkuInfo::getStock);
        IPage<SkuInfo> skuInfoPage = baseMapper.selectPage(page, wrapper);
        return skuInfoPage.getRecords();
    }

    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo) {
        // 1、判断集合是否为空
        if (CollectionUtils.isEmpty(skuStockLockVoList)) {
            throw new DdsysException(ResultCodeEnum.DATA_ERROR);
        }
        // 2、遍历集合，得到里面的每个商品，验证库存并且锁定库存，具备原子性
        skuStockLockVoList.stream().forEach(this::checkLock);
        // 3、只要有一个商品锁定失败，所有锁定成功的商品都解锁
        boolean flag = skuStockLockVoList.stream().anyMatch(skuStockLockVo -> !skuStockLockVo.getIsLock());
        if (flag) {
            // 所有锁定成功的商品都解锁
            skuStockLockVoList.stream().filter(SkuStockLockVo::getIsLock).forEach(skuStockLockVo -> {
                baseMapper.unlockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            });
            // 响应锁定状态
            return false;
        }
        // 4、如果所有商品都锁定成功，redis缓存所有的数据，方便后面解锁和减库存
        // 以orderToken作为key，以lockVos锁定信息作为value
        redisTemplate.opsForValue().set(RedisConst.SROCK_INFO + orderNo, skuStockLockVoList);
        return true;
    }

    private void checkLock(SkuStockLockVo skuStockLockVo) {
        // 公平锁，就是保证客户端获取锁的顺序，跟他们请求获取锁的顺序，是一样的。
        // 公平锁需要排队，谁先申请获取这把锁，
        // 谁就可以先获取到这把锁，是按照请求的先后顺序来的。
        RLock rLock = redissonClient.getFairLock(RedisConst.SKUKEY_PREFIX + skuStockLockVo.getSkuId());
        // 加锁
        rLock.lock();
        try {
            // 验证库存：查询，返回的是满足要求的库存列表
            SkuInfo skuInfo = baseMapper.checkStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            // 如果没有一个仓库满足要求，这里就验库存失败
            if (null == skuInfo) {
                skuStockLockVo.setIsLock(false);
                return;
            }
            // 锁定库存：更新
            Integer row = baseMapper.lockStock(skuStockLockVo.getSkuId(), skuStockLockVo.getSkuNum());
            if (row == 1) {
                skuStockLockVo.setIsLock(true);
            }

        } finally {
            rLock.unlock();
        }
    }
}
