package com.example.back_end.core.admin.address.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WardResponse {

    private String code;

    private String fullName;

}
