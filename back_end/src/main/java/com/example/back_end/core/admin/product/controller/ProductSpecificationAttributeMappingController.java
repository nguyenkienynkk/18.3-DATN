package com.example.back_end.core.admin.product.controller;

import com.example.back_end.core.admin.product.payload.request.ProductSpecificationAttributeMappingRequest;
import com.example.back_end.core.admin.product.payload.request.ProductSpecificationAttributeMappingUpdateRequest;
import com.example.back_end.core.admin.product.payload.response.ProductSpecificationAttributeMappingResponse;
import com.example.back_end.core.admin.product.payload.response.ProductSpecificationAttributeMappingUpdateResponse;
import com.example.back_end.core.admin.product.service.ProductSpecificationAttributeMappingService;
import com.example.back_end.core.common.PageResponse;
import com.example.back_end.core.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/product-specification-attribute-mappings")
public class ProductSpecificationAttributeMappingController {

    private final ProductSpecificationAttributeMappingService productSpecificationAttributeMappingService;

    @PostMapping
    public ResponseData<ProductSpecificationAttributeMappingResponse> createProductSpecificationAttributeMapping(
            @Valid @RequestBody ProductSpecificationAttributeMappingRequest dto) {

        ProductSpecificationAttributeMappingResponse response = productSpecificationAttributeMappingService
                .createProductSpecificationAttributeMapping(dto);

        return ResponseData.<ProductSpecificationAttributeMappingResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Product specification attribute mapping created successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ResponseData<PageResponse<List<ProductSpecificationAttributeMappingResponse>>> getAll(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {

        PageResponse<List<ProductSpecificationAttributeMappingResponse>> response =
                productSpecificationAttributeMappingService
                        .getAllProductSpecificationAttributeMapping(name, pageNo, pageSize);

        return ResponseData.<PageResponse<List<ProductSpecificationAttributeMappingResponse>>>builder()
                .status(HttpStatus.OK.value())
                .message("Get product specification attribute mappings success")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseData<ProductSpecificationAttributeMappingResponse> getById(@PathVariable Long id) {

        ProductSpecificationAttributeMappingResponse response = productSpecificationAttributeMappingService
                .getProductSpecificationAttributeMappingById(id);

        return ResponseData.<ProductSpecificationAttributeMappingResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Get product specification attribute mapping by ID success")
                .data(response)
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete product specification attribute mappings",
            description = "Send a request via this API to delete product specification attribute mappings")
    @DeleteMapping
    public ResponseData<Void> deleteProductSpecificationAttributeMappings(@RequestBody List<Long> ids) {

        productSpecificationAttributeMappingService.deleteProductSpecificationAttribute(ids);

        return ResponseData.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Delete product specification attribute mappings success")
                .build();
    }

    @GetMapping("/by-product/{productId}")
    public ResponseData<PageResponse<List<ProductSpecificationAttributeMappingResponse>>> getByProductId(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {

        PageResponse<List<ProductSpecificationAttributeMappingResponse>> response =
                productSpecificationAttributeMappingService
                        .getProcSpecMappingsByProductId(productId, pageNo, pageSize);

        return ResponseData.<PageResponse<List<ProductSpecificationAttributeMappingResponse>>>builder()
                .status(HttpStatus.OK.value())
                .message("Get product specification attribute mappings by product ID success")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseData<ProductSpecificationAttributeMappingUpdateResponse> updateProductSpecificationAttributeMapping(
            @PathVariable Long id,
            @Valid @RequestBody ProductSpecificationAttributeMappingUpdateRequest dto) {

        ProductSpecificationAttributeMappingUpdateResponse response =
                productSpecificationAttributeMappingService.updateProductSpecificationAttributeMapping(id, dto);

        return ResponseData.<ProductSpecificationAttributeMappingUpdateResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product specification attribute mapping updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteProductSpecificationAttributeMapping(@PathVariable Long id) {

        productSpecificationAttributeMappingService.deleteProductSpecificationAttributeMappingById(id);

        return ResponseData.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Product specification attribute mapping deleted successfully")
                .build();
    }

}
