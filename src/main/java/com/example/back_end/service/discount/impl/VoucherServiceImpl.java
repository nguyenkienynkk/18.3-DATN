package com.example.back_end.service.discount.impl;

import com.example.back_end.core.admin.customer.payload.response.CustomerResponse;
import com.example.back_end.core.admin.discount.mapper.DiscountMapper;
import com.example.back_end.core.admin.discount.mapper.VoucherMapper;
import com.example.back_end.core.admin.discount.payload.request.DiscountFilterRequest;
import com.example.back_end.core.admin.discount.payload.request.VoucherRequest;
import com.example.back_end.core.admin.discount.payload.request.VoucherUpdateRequest;
import com.example.back_end.core.admin.discount.payload.response.DiscountFullResponse;
import com.example.back_end.core.admin.discount.payload.response.ProductResponseDetails;
import com.example.back_end.core.admin.discount.payload.response.VoucherApplyResponse;
import com.example.back_end.core.admin.discount.payload.response.VoucherApplyResponseWrapper;
import com.example.back_end.core.admin.discount.payload.response.VoucherFullResponse;
import com.example.back_end.core.admin.discount.payload.response.VoucherResponse;
import com.example.back_end.entity.Customer;
import com.example.back_end.entity.CustomerVoucher;
import com.example.back_end.entity.Discount;
import com.example.back_end.entity.DiscountAppliedToProduct;
import com.example.back_end.entity.Product;
import com.example.back_end.infrastructure.constant.DiscountType;
import com.example.back_end.infrastructure.constant.ErrorCode;
import com.example.back_end.infrastructure.exception.InvalidDataException;
import com.example.back_end.infrastructure.exception.NotFoundException;
import com.example.back_end.repository.CustomerRepository;
import com.example.back_end.repository.CustomerVoucherRepository;
import com.example.back_end.repository.DiscountAppliedToProductRepository;
import com.example.back_end.repository.DiscountRepository;
import com.example.back_end.service.discount.EmailService;
import com.example.back_end.service.discount.VoucherService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherServiceImpl implements VoucherService {
    DiscountRepository discountRepository;
    VoucherMapper voucherMapper;
    CustomerRepository customerRepository;
    CustomerVoucherRepository customerVoucherRepository;
    EmailService emailService;
    private static final Random RANDOM = new Random();
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Override
    public List<VoucherResponse> getAllVouchers(DiscountFilterRequest filterRequest) {
        List<Discount> discounts = discountRepository.searchDiscountsNoPage(
                filterRequest.getName(),
                filterRequest.getCouponCode(),
                filterRequest.getDiscountTypeId(),
                filterRequest.getStartDate(),
                filterRequest.getEndDate(),
                filterRequest.getStatus(),
                filterRequest.getIsPublished()
        );
        discounts.forEach(this::updateVoucherStatus);
        return voucherMapper.toResponseList(discounts);
    }

    public static String generateVoucherCode(String prefix, Long id) {
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return prefix + id + randomPart;
    }

    public void updateVoucherStatus(Discount discount) {
        updateStatusVoucher(discount, discountRepository);
    }

    public static void updateStatusVoucher(Discount discount, DiscountRepository discountRepository) {
        Instant now = Instant.now();
        if (discount.getIsCanceled() != null && discount.getIsCanceled()) {
            discount.setStatus("CANCEL");
        } else if (discount.getEndDateUtc() != null && now.isAfter(discount.getEndDateUtc())) {
            discount.setStatus("EXPIRED");
        } else if (discount.getStartDateUtc() != null && now.isBefore(discount.getStartDateUtc())) {
            discount.setStatus("UPCOMING");
        } else {
            discount.setStatus("ACTIVE");
        }
        discountRepository.save(discount);
    }

    @Override
    @Transactional
    public void createDiscount(VoucherRequest voucherRequest) {
        checkDuplicateCouponCode(voucherRequest.getCouponCode(), voucherRequest.getIsPublished());
        Discount discount = voucherMapper.toEntity(voucherRequest);
        validateDiscount(discount);

        if (discount.getStartDateUtc() == null)
            discount.setStartDateUtc(Instant.now());

        if (discount.getEndDateUtc() == null)
            discount.setEndDateUtc(discount.getStartDateUtc().plusSeconds(86400));

        updateVoucherStatus(discount);
        discount = discountRepository.save(discount);
        saveDiscountAppliedToCustomers(discount, voucherRequest.getSelectedCustomerIds());
        sendVoucherEmailsToCustomers(
                voucherRequest.getSelectedCustomerIds(),
                discount.getCouponCode(),
                discount.getName(),
                discount.getStartDateUtc(),
                discount.getEndDateUtc(),
                discount.getDiscountPercentage(),
                discount.getDiscountAmount());
    }

    private void checkDuplicateCouponCode(String couponCode, Boolean isPublished) {
        if (Boolean.FALSE.equals(isPublished)) {
            if (couponCode == null || couponCode.trim().isEmpty()) {
                throw new InvalidDataException("Coupon code must not be null or empty for unpublished vouchers.");
            }
            if (discountRepository.existsByCouponCode(couponCode)) {
                throw new InvalidDataException("Coupon code already exists: " + couponCode);
            }
        }
    }


    private void sendVoucherEmailsToCustomers(List<Long> customerIds, String voucherCode, String discountDetails, Instant startDate, Instant endDate, BigDecimal discountPercentage, BigDecimal discountAmount) {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            log.warn("Voucher code is missing, no email will be sent.");
            return;
        }

        List<String> customerEmails = customerRepository.findByIdIn(customerIds).stream()
                .map(Customer::getEmail)
                .toList();

        try {
            if (discountPercentage != null) {
                emailService.sendVoucherEmails(customerEmails, voucherCode, discountDetails, startDate, endDate, discountPercentage, null);
                log.info("Voucher emails with percentage sent successfully.");
            } else if (discountAmount != null) {
                emailService.sendVoucherEmails(customerEmails, voucherCode, discountDetails, startDate, endDate, null, discountAmount);
                log.info("Voucher emails with fixed amount sent successfully.");
            } else {
                log.warn("Neither discount percentage nor discount amount found, no email sent.");
            }
        } catch (MessagingException e) {
            log.error("Failed to send voucher emails.", e);
        }
    }

    @Scheduled(cron = "0 1 0 * * *")
    @Override
    @Transactional
    public void checkAndGenerateBirthdayVoucher() {
        LocalDate today = LocalDate.now();
        List<Customer> customers = customerRepository.findAllByBirthday(today);
        for (Customer customer : customers) {
            createBirthdayVoucher(customer);
        }
        log.info("Processed birthday vouchers for {} customers", customers.size());
    }

    @Transactional
    public void createBirthdayVoucher(Customer customer) {
        BigDecimal discountPercent = BigDecimal.TEN;
        Instant startDate = Instant.now();
        Instant endDate = startDate.plusSeconds(86400L * 5);
        VoucherRequest voucherRequest = VoucherRequest.builder()
                .name("Birthday Voucher for " + customer.getFirstName() + " " + customer.getLastName())
                .couponCode(generateVoucherCode("BDAY_", customer.getId()))
                .discountPercentage(discountPercent)
                .startDateUtc(startDate)
                .endDateUtc(endDate)
                .limitationTimes(1)
                .comment("Happy birthday")
                .isPublished(false)
                .minOderAmount(BigDecimal.ZERO)
                .isCumulative(false)
                .maxDiscountAmount(BigDecimal.valueOf(100000))
                .discountTypeId(DiscountType.ASSIGNED_TO_ORDER_TOTAL)
                .usePercentage(true)
                .selectedCustomerIds(List.of(customer.getId()))
                .requiresCouponCode(true)
                .build();
        createDiscount(voucherRequest);
        log.info("Created birthday voucher for customer: {}", customer.getEmail());
    }

    private void validateDiscount(Discount discount) {
        validateDiscountAmount(discount);
        validateCouponCodeRequirement(discount);
        validateDate(discount.getStartDateUtc(), discount.getEndDateUtc());
        validateDiscountNameLength(discount.getName());
        validateMaxDiscountAmount(discount);
    }

    private void validateDiscountAmount(Discount discount) {
        Boolean usePercentage = discount.getUsePercentage();
        BigDecimal discountPercentage = discount.getDiscountPercentage();
        BigDecimal discountAmount = discount.getDiscountAmount();
        if (usePercentage != null && usePercentage) {
            if (discountAmount != null) {
                throw new IllegalArgumentException(
                        "When 'usePercentage' is true, 'discountAmount' should not be provided.");
            }
        } else {
            if (discountPercentage != null) {
                throw new IllegalArgumentException(
                        "When 'usePercentage' is false, 'discountPercentage' should not be provided.");
            }
        }
    }

    private void validateDiscountNameLength(String discountName) {
        if (discountName.length() > 50) {
            throw new IllegalArgumentException("The 'name' must not exceed 50 characters.");
        }
    }

    private void validateMaxDiscountAmount(Discount discount) {
        BigDecimal maxDiscountAmount = discount.getMaxDiscountAmount();
        if (maxDiscountAmount != null && maxDiscountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The 'maxDiscountAmount' must be a positive value.");
        }
    }

    private void validateCouponCodeRequirement(Discount discount) {
        Predicate<Discount> requiresCouponCodeCheck = d ->
                d.getRequiresCouponCode() != null && d.getRequiresCouponCode();

        Runnable couponCodeValidation = () -> {
            if (discount.getCouponCode() == null || discount.getCouponCode().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "If 'requiresCouponCode' is true, 'couponCode' cannot be null or empty.");
            }
        };

        if (requiresCouponCodeCheck.test(discount)) {
            couponCodeValidation.run();
        }
    }

    private void validateDate(Instant startDate, Instant endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidDataException("The 'startDateUtc' must be before 'endDateUtc'.");
        }
    }

    private void saveDiscountAppliedToCustomers(Discount discount, List<Long> selectedCustomerIds) {
        initializeUsageCount(discount);
        for (Long customerId : selectedCustomerIds) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("Customer not found with ID: " + customerId));
            CustomerVoucher customerVoucher = CustomerVoucher.builder()
                    .customer(customer)
                    .discount(discount)
                    .build();
            customerVoucherRepository.save(customerVoucher);
        }
    }

    private void initializeUsageCount(Discount discount) {
        if (discount.getUsageCount() == null) {
            discount.setUsageCount(discount.getLimitationTimes());
            discountRepository.save(discount);
        } else if (discount.getUsageCount() <= 0) {
            throw new InvalidDataException("Voucher has been fully redeemed.");
        }
    }

    @Transactional
    @Override
    public void updateVoucher(Long voucherId, VoucherUpdateRequest request) {
        Discount discount = discountRepository.findById(voucherId)
                .orElseThrow(() -> new NotFoundException("Voucher not found with ID: " + voucherId));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            validateDiscountNameLength(request.getName());
            discount.setName(request.getName());
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            validateDate(request.getStartDate(), request.getEndDate());
            discount.setStartDateUtc(request.getStartDate());
            discount.setEndDateUtc(request.getEndDate());
        }

        if (request.getMaxUsageCount() != null) {
            if (discount.getUsageCount() != null && request.getMaxUsageCount() < discount.getUsageCount()) {
                throw new InvalidDataException(
                        "Cannot reduce max usage count below the current usage count (" + discount.getUsageCount() + ")."
                );
            }
            if ("ACTIVE".equals(discount.getStatus()) && request.getMaxUsageCount() < discount.getLimitationTimes()) {
                throw new InvalidDataException(
                        "Cannot reduce max usage count while the voucher is ACTIVE."
                );
            }
            discount.setLimitationTimes(request.getMaxUsageCount());
            initializeUsageCount(discount);
        }
        discountRepository.save(discount);
        log.info("Updated voucher with ID: {}", voucherId);
    }

    @Override
    public VoucherFullResponse getVoucherById(Long id) {
        Discount discount = findDiscountById(id);
        updateStatusVoucher(discount, discountRepository);

        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByDiscount(discount);
        List<CustomerResponse> appliedCustomers = customerVouchers.stream()
                .map(customerVoucher -> {
                    Customer customer = customerVoucher.getCustomer();
                    return CustomerResponse.builder()
                            .id(customer.getId())
                            .firstName(customer.getFirstName())
                            .lastName(customer.getLastName())
                            .email(customer.getEmail())
                            .build();
                })
                .toList();

        VoucherFullResponse response = voucherMapper.toFullResponse(discount);
        response.setAppliedCustomers(appliedCustomers);

        return response;
    }


    private Discount findDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DISCOUNT_NOT_FOUND.getMessage()));
    }


    //voucher applied to order

    private void validatePrivateVoucherForEmail(String email, Discount discount) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer not found with email: " + email));
        boolean isVoucherAssignedToCustomer = customerVoucherRepository.existsByCustomerAndDiscount(customer, discount);
        if (!isVoucherAssignedToCustomer) {
            throw new InvalidDataException("Voucher is private and not assigned to this email.");
        }
    }

    private void validateVoucherApplicability(BigDecimal subTotal, Discount discount, Instant now) {
        if (Boolean.TRUE.equals(discount.getIsCanceled())) {
            throw new InvalidDataException("Voucher has been canceled.");
        }

        if (discount.getEndDateUtc() != null && now.isAfter(discount.getEndDateUtc())) {
            throw new InvalidDataException("Voucher has expired.");
        }

        if (discount.getStartDateUtc() != null && now.isBefore(discount.getStartDateUtc())) {
            throw new InvalidDataException("Voucher is not yet valid.");
        }

        if (discount.getMinOderAmount() != null &&
                subTotal.compareTo(discount.getMinOderAmount()) < 0) {
            throw new InvalidDataException("Order does not meet the minimum amount for the voucher.");
        }
    }

    private BigDecimal calculateDiscount(BigDecimal subTotal, Discount discount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discount.getUsePercentage() != null && discount.getUsePercentage()) {
            discountAmount = subTotal
                    .multiply(discount.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
        } else if (discount.getDiscountAmount() != null) {
            discountAmount = discount.getDiscountAmount();
        }
        if (discount.getMaxDiscountAmount() != null) {
            discountAmount = discountAmount.min(discount.getMaxDiscountAmount());
        }
        return discountAmount;
    }

    @Override
    @Transactional
    public VoucherApplyResponseWrapper validateAndCalculateDiscounts(BigDecimal subTotal, List<String> couponCodes, String email) {
        Instant now = Instant.now();
        List<VoucherApplyResponse> responses = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<Long> applicableVoucherIds = new ArrayList<>();
        Set<String> processedCoupons = new HashSet<>();

        boolean hasNonCumulativeVoucher = false;

        for (String code : couponCodes) {
            if (processedCoupons.contains(code)) {
                continue;
            }
            processedCoupons.add(code);

            VoucherApplyResponse response = VoucherApplyResponse.builder()
                    .couponCode(code)
                    .build();
            try {
                Discount discount = discountRepository.findByCouponCode(code);
                if (discount == null) {
                    response.setIsApplicable(false);
                    response.setReason("Coupon code not found.");
                    responses.add(response);
                    continue;
                }

                if (Boolean.TRUE.equals(!discount.getIsCumulative()) && !applicableVoucherIds.isEmpty()) {
                    response.setIsApplicable(false);
                    response.setReason("This voucher cannot be combined with others.");
                    responses.add(response);
                    continue;
                }

                if (hasNonCumulativeVoucher) {
                    response.setIsApplicable(false);
                    response.setReason("Cannot add more vouchers because a non-cumulative voucher is already applied.");
                    responses.add(response);
                    continue;
                }

                if (Boolean.TRUE.equals(!discount.getIsCumulative())) {
                    hasNonCumulativeVoucher = true;
                }

                if (Boolean.TRUE.equals(discount.getRequiresCouponCode()) && Boolean.TRUE.equals(!discount.getIsPublished())) {
                    if (email != null) {
                        validatePrivateVoucherForEmail(email, discount);
                    } else {
                        response.setIsApplicable(false);
                        response.setReason("Voucher is private. Please provide an email.");
                        responses.add(response);
                        continue;
                    }
                }

                validateVoucherApplicability(subTotal, discount, now);

                BigDecimal discountAmount = calculateDiscount(subTotal, discount);
                totalDiscount = totalDiscount.add(discountAmount);
                response.setIsApplicable(true);
                response.setDiscountAmount(Boolean.TRUE.equals(discount.getUsePercentage()) ? null : discountAmount);
                response.setDiscountPercent(Boolean.TRUE.equals(discount.getUsePercentage()) ? discount.getDiscountPercentage() : null);
                response.setId(discount.getId());
                response.setMaxDiscountAmount(discount.getMaxDiscountAmount());

                applicableVoucherIds.add(discount.getId());
            } catch (InvalidDataException | NotFoundException e) {
                response.setIsApplicable(false);
                response.setReason(e.getMessage());
            }

            responses.add(response);
        }
        applicableVoucherIds = new ArrayList<>(new LinkedHashSet<>(applicableVoucherIds));

        return VoucherApplyResponseWrapper.builder()
                .totalDiscount(totalDiscount)
                .voucherResponses(responses)
                .applicableVoucherIds(applicableVoucherIds)
                .build();
    }


}
