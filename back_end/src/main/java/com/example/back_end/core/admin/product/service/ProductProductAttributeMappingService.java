package com.example.back_end.core.admin.product.service;

import com.example.back_end.core.admin.product.payload.request.ProductProductAttributeMappingRequest;
import com.example.back_end.core.admin.product.payload.response.ProductProductAttributeMappingDetailResponse;
import com.example.back_end.core.common.PageResponse;

public interface ProductProductAttributeMappingService {

    PageResponse<?> getProductProductAttributeMappings(Long productId, int pageNo, int pageSize);

    ProductProductAttributeMappingDetailResponse getProductProductAttributeMapping(Long id);

    void addProductProductAttributeMapping(ProductProductAttributeMappingRequest request);

    void updateProductProductAttributeMapping(Long id, ProductProductAttributeMappingRequest request);

    void deleteProductProductAttributeMapping(Long id);

}
