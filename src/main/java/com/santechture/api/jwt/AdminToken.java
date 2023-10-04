package com.santechture.api.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AdminToken {
    private Integer id;
    private String username;
}
