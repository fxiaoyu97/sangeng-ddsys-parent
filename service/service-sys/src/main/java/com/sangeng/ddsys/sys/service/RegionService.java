package com.sangeng.ddsys.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.sys.Region;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface RegionService extends IService<Region> {

    List<Region> findRegionByKeyword(String keyword);
}
