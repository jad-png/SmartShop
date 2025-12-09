package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.response.UserDTO;
import com.jyoxin.smartshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    UserDTO toDTO(User user);
}