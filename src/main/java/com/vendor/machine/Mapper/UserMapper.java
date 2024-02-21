package com.vendor.machine.Mapper;

import org.springframework.stereotype.Component;

import com.vendor.machine.DTO.UpdateUserDTO;
import com.vendor.machine.Entity.User;

@Component
public class UserMapper {
    
    public User toUser(UpdateUserDTO user) {
        User dto = new User();
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
