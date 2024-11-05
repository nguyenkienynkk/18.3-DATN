package com.example.back_end.core.admin.product.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationAttributeGroupRequest {
    @NotBlank
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private Integer displayOrder;

}