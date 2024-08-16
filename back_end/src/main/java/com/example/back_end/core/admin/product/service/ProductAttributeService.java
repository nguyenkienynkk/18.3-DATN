package com.example.back_end.core.admin.product.service;

import com.example.back_end.core.admin.product.payload.request.ProductAttributeRequest;
import com.example.back_end.core.admin.product.payload.response.ProductAttributeNameResponse;
import com.example.back_end.core.admin.product.payload.response.ProductAttributeResponse;
import com.example.back_end.core.common.PageResponse;
import com.example.back_end.entity.ProductAttribute;

import java.util.List;

public interface ProductAttributeService {

    ProductAttribute createProductAttribute(ProductAttributeRequest request);

    PageResponse<List<ProductAttributeResponse>> getAllProductAttribute(String name, int pageNo, int pageSize);

    ProductAttributeResponse getProductAttributeById(Long id);

    ProductAttributeResponse updateProductAttribute(Long id, ProductAttributeRequest request);

    void deleteProductAttribute(List<Long> ids);

    List<ProductAttributeNameResponse> getAttributeName();

}