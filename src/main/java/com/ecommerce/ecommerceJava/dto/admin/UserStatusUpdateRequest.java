package com.ecommerce.ecommerceJava.dto.admin;

import com.ecommerce.ecommerceJava.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusUpdateRequest {
    private Role role;
    private Boolean enabled;
}
