package com.sangeng.ddsys.sys.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.common.exception.DdsysException;
import com.sangeng.ddsys.common.result.ResultCodeEnum;
import com.sangeng.ddsys.model.sys.RegionWare;
import com.sangeng.ddsys.sys.mapper.RegionWareMapper;
import com.sangeng.ddsys.sys.service.RegionWareService;
import com.sangeng.ddsys.vo.sys.RegionWareQueryVo;

/**
 * <p>
 * 城市仓库关联表 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class RegionWareServiceImp extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {
    @Override
    public IPage<RegionWare> selectPageRegionWare(Page<RegionWare> regionWarePage,
        RegionWareQueryVo regionWareQueryVo) {
        // 1、获取查询条件的值
        String keyWord = regionWareQueryVo.getKeyword();
        // 2、判断条件值是否为空
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyWord)) {
            // 根据区域名称或者仓库名称查询
            wrapper.like(RegionWare::getRegionName, keyWord).or().like(RegionWare::getWareName, keyWord);
        }
        // 3、调用方法实现分页查询
        return baseMapper.selectPage(regionWarePage, wrapper);
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        // 判断区域是否已经开通
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        Long count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            // 如果已经开通，则抛出异常
            throw new DdsysException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}
