package com.sangeng.ddsys.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.sys.Region;
import com.sangeng.ddsys.sys.mapper.RegionMapper;
import com.sangeng.ddsys.sys.service.RegionService;

/**
 * <p>
 * 地区表 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    @Override
    public List<Region> findRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Region::getName, keyword);
        return baseMapper.selectList(queryWrapper);
    }
}
