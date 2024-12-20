package com.example.back_end.core.admin.product.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductAttributeValueResponse {

    private Long id;

    private String name;

    private String colorSquaresRgb;

    private BigDecimal priceAdjustment;

    private Boolean priceAdjustmentPercentage;

    private BigDecimal weightAdjustment;

    private BigDecimal cost;

    private Boolean isPreSelected;

    private Integer displayOrder;

    private List<String> imageUrl = new ArrayList<>();

}
