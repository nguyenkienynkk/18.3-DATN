package com.example.back_end.core.admin.customer.service.impl;

import com.example.back_end.core.admin.customer.payload.response.ProvinceResponse;
import com.example.back_end.core.admin.customer.service.ProvinceService;
import com.example.back_end.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;

    @Override
    public List<ProvinceResponse> getAllProvince() {
        return provinceRepository.findAll().stream()
                .map(province -> ProvinceResponse.builder()
                        .code(province.getCode())
                        .nameEn(province.getNameEn())
                        .build()
                )
                .toList();
    }
}