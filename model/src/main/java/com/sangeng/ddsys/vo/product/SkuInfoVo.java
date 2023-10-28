package com.sangeng.ddsys.vo.product;

import java.util.List;

import com.sangeng.ddsys.model.product.SkuAttrValue;
import com.sangeng.ddsys.model.product.SkuImage;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.model.product.SkuPoster;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SkuInfoVo extends SkuInfo {

    @ApiModelProperty(value = "海报列表")
    private List<SkuPoster> skuPosterList;

    @ApiModelProperty(value = "属性值")
    private List<SkuAttrValue> skuAttrValueList;

    @ApiModelProperty(value = "图片")
    private List<SkuImage> skuImagesList;

}
