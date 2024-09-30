package com.example.back_end.core.admin.customer.payload.response;

import com.example.back_end.infrastructure.constant.GenderType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CustomerFullResponse {

    private UUID customerGuid;

    private String email;

    private String firstName;

    private String lastName;

    private GenderType gender;

    private Instant dateOfBirth;

    private List<Long> customerRoles;

    private String username;

    private Boolean active;

    private String phone;

    private Boolean hasShoppingCartItems;

    private Boolean requireReLogin;

    private Integer failedLoginAttempts;

    private Instant cannotLoginUntilDateUtc;

    private Boolean deleted;

    private Instant lastLoginDateUtc;

    private Instant lastActivityDateUtc;

}