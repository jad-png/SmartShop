package com.jyoxin.smartshop.dto.response;

import lombok.*;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String role;

    // client fields
    private Long clientId;
    private String clientName;
}