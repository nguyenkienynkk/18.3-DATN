package com.example.back_end.core.admin.product.controller;

import com.example.back_end.core.admin.product.payload.request.SpecificationAttributeOptionRequest;
import com.example.back_end.core.admin.product.payload.response.SpecificationAttributeOptionNameResponse;
import com.example.back_end.core.admin.product.payload.response.SpecificationAttributeOptionResponse;
import com.example.back_end.service.product.SpecificationAttributeOptionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/specification-attribute-options")
public class SpecificationAttributeOptionController {

    private final SpecificationAttributeOptionService specificationAttributeOptionService;

    @Operation(method = "POST", summary = "Add new specification attribute option",
            description = "Send a request via this API to create new specification attribute option")
    @PostMapping
    public ResponseData<SpecificationAttributeOptionResponse> createSpecificationAttributeOption(
            @Valid @RequestBody SpecificationAttributeOptionRequest dto
    ) {

        SpecificationAttributeOptionResponse response =
                specificationAttributeOptionService.createSpecificationAttributeOption(dto);

        return ResponseData.<SpecificationAttributeOptionResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Specification attribute created successfully")
                .data(response)
                .build();

    }
    @Operation(method = "GET", summary = "Get all options by specification attribute ID",
            description = "Send a request via this API to get all options by specification attribute ID")
    @GetMapping("/by-spec/{specificationAttributeId}")
    public ResponseData<List<SpecificationAttributeOptionNameResponse>> getAllOptionsBySpecificationAttributeId(
            @PathVariable Long specificationAttributeId) {

        List<SpecificationAttributeOptionNameResponse> response =
                specificationAttributeOptionService.getAllOptionsBySpecificationAttributeId(specificationAttributeId);

        return ResponseData.<List<SpecificationAttributeOptionNameResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get specification attribute options by specification attribute ID success")
                .data(response)
                .build();
    }

    @Operation(method = "GET", summary = "Get all specification attribute options",
            description = "Send a request via this API to get all specification attribute options")
    @GetMapping
    public ResponseData<PageResponse<List<SpecificationAttributeOptionResponse>>> getAll(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {

        PageResponse<List<SpecificationAttributeOptionResponse>> response = specificationAttributeOptionService
                .getAllSpecificationAttributeOption(name, pageNo, pageSize);

        return ResponseData.<PageResponse<List<SpecificationAttributeOptionResponse>>>builder()
                .status(HttpStatus.OK.value())
                .message("Get specification attribute options success")
                .data(response)
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete specification attribute options",
            description = "Send a request via this API to delete specification attribute options")
    @DeleteMapping
    public ResponseData<Void> deleteSpecificationAttributeOptions(@RequestBody List<Long> ids) {

        specificationAttributeOptionService.deleteSpecificationAttributeOption(ids);

        return ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Delete specification attribute options success")
                .build();
    }

}
