package com.sangeng.ddsys.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.sys.RegionWare;
import com.sangeng.ddsys.vo.sys.RegionWareQueryVo;

/**
 * <p>
 * 城市仓库关联表 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface RegionWareService extends IService<RegionWare> {

    IPage<RegionWare> selectPageRegionWare(Page<RegionWare> regionWarePage, RegionWareQueryVo regionWareQueryVo);

    void saveRegionWare(RegionWare regionWare);

    void updateStatus(Long id, Integer status);
}
