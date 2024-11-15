package com.example.back_end.service.returnProducts.impl;

import com.example.back_end.core.admin.returnProduct.mapper.ReturnInvoiceMapper;
import com.example.back_end.core.admin.returnProduct.payload.request.ReturnInvoiceRequest;
import com.example.back_end.core.admin.returnProduct.payload.response.ReturnInvoiceResponse;
import com.example.back_end.core.common.PageRequest;
import com.example.back_end.core.common.PageResponse1;
import com.example.back_end.entity.ReturnInvoice;
import com.example.back_end.infrastructure.utils.PageUtils;
import com.example.back_end.repository.ReturnInvoiceRepository;
import com.example.back_end.service.returnProducts.ReturnInvoiceServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReturnInvoiceServicesImpl implements ReturnInvoiceServices {
    private final ReturnInvoiceRepository repository;
    private final ReturnInvoiceMapper mapper;

    @Override
    public ReturnInvoiceResponse saveReturnInvoice(ReturnInvoiceRequest returnInvoiceRequest) {
        ReturnInvoice returnInvoice = repository.save(mapper.mapReturnInvoice(returnInvoiceRequest));
        return mapper.mapReturnInvoiceResponse(returnInvoice);
    }

    @Override
    public ReturnInvoiceResponse getReturnInvoiceById(Long id) {
        ReturnInvoice returnInvoice = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return Invoice not found with id: " + id));
        return mapper.mapReturnInvoiceResponse(returnInvoice);
    }

    @Override
    public ReturnInvoiceResponse getReturnInvoiceByOrderId(Long orderId) {
        Optional<ReturnInvoice> result = Optional.ofNullable(repository.findByOrderId(orderId));
        ReturnInvoice returnInvoice = result.orElseThrow(() -> new RuntimeException("Return Invoice not found with id: " + orderId));
        return mapper.mapReturnInvoiceResponse(returnInvoice);
    }

    @Override
    public PageResponse1<List<ReturnInvoiceResponse>> getAllReturnInvoices(PageRequest pageRequest) {
        Pageable pageable = PageUtils.createPageable(
                pageRequest.getPageNo(),
                pageRequest.getPageSize(),
                pageRequest.getSortBy(),
                pageRequest.getSortDir());
        Page<ReturnInvoice> result = repository.findAll(pageable);
        List<ReturnInvoiceResponse> responses = mapper.mapReturnInvoices(result.getContent());
        return PageResponse1.<List<ReturnInvoiceResponse>>builder()
                .totalItems(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .items(responses)
                .build();
    }
}
