package com.sangeng.ddsys.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author calos
 * @version 1.0.0
 * @createTime 2023/11/4 8:29
 **/
public interface FileUploadService {
    Object fileUpload(MultipartFile file);
}
