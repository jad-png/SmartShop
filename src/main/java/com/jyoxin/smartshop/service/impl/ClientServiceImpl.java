package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.core.exception.UnauthorizedException;
import com.jyoxin.smartshop.dto.request.CreateClientRequest;
import com.jyoxin.smartshop.dto.request.UpdateClientRequest;
import com.jyoxin.smartshop.dto.response.ClientDTO;
import com.jyoxin.smartshop.entity.Client;
import com.jyoxin.smartshop.entity.ClientStats;
import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.entity.enums.Tier;
import com.jyoxin.smartshop.mapper.ClientMapper;
import com.jyoxin.smartshop.repository.ClientRepository;
import com.jyoxin.smartshop.repository.UserRepository;
import com.jyoxin.smartshop.service.ClientService;
import com.jyoxin.smartshop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClientDTO createClient(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UnauthorizedException("Username already in use");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(PasswordUtil.hash(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        Client newClient = clientMapper.toEntity(request);
        newClient.setUser(newUser);
        newClient.setStats(ClientStats.empty());
        newClient.setLoyaltyTier(Tier.BASIC);

        Client savedClient = clientRepository.save(newClient);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }

    @Override
    public ClientDTO getClientByUserId(Long userId) {
        return clientRepository.findByUserId(userId)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "userId", userId));
    }

    @Override
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(clientMapper::toResponse);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        if (!client.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already in use");
        }

        clientMapper.updateEntityFromRequest(request, client);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        clientRepository.delete(client);

        User user = client.getUser();
        if (user != null) {
            user.setClient(null);
            user.setDeleted(true);
            client.setUser(null);
            userRepository.save(user);
        }
    }
}