package com.jyoxin.smartshop.service;


import com.jyoxin.smartshop.dto.request.CreateClientRequest;
import com.jyoxin.smartshop.dto.request.UpdateClientRequest;
import com.jyoxin.smartshop.dto.response.ClientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientDTO createClient(CreateClientRequest request);

    ClientDTO getClientById(Long id);

    ClientDTO getClientByUserId(Long userId);

    Page<ClientDTO> getAllClients(Pageable pageable);

    ClientDTO updateClient(Long id, UpdateClientRequest request);

    void deleteClient(Long id);
}