package com.jyoxin.smartshop.mapper;

import com.jyoxin.smartshop.dto.request.CreateClientRequest;
import com.jyoxin.smartshop.dto.request.UpdateClientRequest;
import com.jyoxin.smartshop.dto.response.ClientDTO;
import com.jyoxin.smartshop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    
    Client toEntity(CreateClientRequest request);


    void updateEntityFromRequest(UpdateClientRequest request, @MappingTarget Client client);

    ClientDTO toResponse(Client client);
}