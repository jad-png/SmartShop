package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.dto.request.CreateClientRequest;
import com.jyoxin.smartshop.dto.request.UpdateClientRequest;
import com.jyoxin.smartshop.dto.response.ClientDTO;
import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.AuthService;
import com.jyoxin.smartshop.service.ClientService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final AuthService authService;

    @PostMapping
    @HasRole(Role.ADMIN)
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody CreateClientRequest request) {
        return new ResponseEntity<>(clientService.createClient(request), HttpStatus.CREATED);
    }

    @GetMapping
    @HasRole(Role.ADMIN)
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable) {
        return ResponseEntity.ok(clientService.getAllClients(pageable));
    }

    @GetMapping("/{id}")
    @HasRole(Role.ADMIN)
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/{id}")
    @HasRole(Role.ADMIN)
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    @HasRole(Role.ADMIN)
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    @Authenticated
    @HasRole(Role.CLIENT)
    public ResponseEntity<ClientDTO> getMyProfile(HttpSession session) {
        var currentUser = authService.getCurrentUser(session);
        return ResponseEntity.ok(clientService.getClientByUserId(currentUser.getId()));
    }
}