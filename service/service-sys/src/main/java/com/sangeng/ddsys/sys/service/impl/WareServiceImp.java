package com.sangeng.ddsys.sys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.sys.Ware;
import com.sangeng.ddsys.sys.mapper.WareMapper;
import com.sangeng.ddsys.sys.service.WareService;

/**
 * <p>
 * 仓库表 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class WareServiceImp extends ServiceImpl<WareMapper, Ware> implements WareService {

}
