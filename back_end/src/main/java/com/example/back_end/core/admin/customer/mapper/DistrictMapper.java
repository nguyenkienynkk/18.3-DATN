package com.example.back_end.core.admin.customer.mapper;

import com.example.back_end.core.admin.customer.payload.response.DistrictResponse;
import com.example.back_end.entity.District;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DistrictMapper {

    @Mapping(target = "provinceCode", source = "provinceCode.code")
    DistrictResponse toResponse(District district);

    List<DistrictResponse> toResponseList(List<District> districts);

}
