package com.jyoxin.smartshop.core.seeder;

import com.jyoxin.smartshop.entity.Client;
import com.jyoxin.smartshop.entity.ClientStats;
import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.entity.enums.Tier;
import com.jyoxin.smartshop.repository.ClientRepository;
import com.jyoxin.smartshop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class ClientSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {
        if (clientRepository.count() == 0) {
            log.info("Seeding clients with users...");

            seedClients();

            log.info("{} clients seeded successfully", clientRepository.count());
        } else {
            log.info("Clients already exist, skipping...");
        }
    }

    private void seedClients() {
        List<ClientData> clients = List.of(
                new ClientData("Drake Graham", "drake@ovo.com", "drake",
                        Tier.BASIC, 0, BigDecimal.ZERO, null, null),
                new ClientData("Kendrick Lamar", "kdot@pgland.com", "kendrick",
                        Tier.BASIC, 2, new BigDecimal("1500.00"),
                        LocalDate.now().minusDays(15), LocalDate.now().minusDays(5)),
                new ClientData("Travis Scott", "travis@cactusjack.com", "travis",
                        Tier.BASIC, 1, new BigDecimal("800.00"),
                        LocalDate.now().minusDays(7), LocalDate.now().minusDays(7)),

                new ClientData("Kanye West", "ye@yeezy.com", "kanye",
                        Tier.SILVER, 8, new BigDecimal("12500.00"),
                        LocalDate.now().minusMonths(3), LocalDate.now().minusDays(2)),
                new ClientData("Snoop Dogg", "snoop@deathrow.com", "snoop",
                        Tier.SILVER, 12, new BigDecimal("18000.00"),
                        LocalDate.now().minusMonths(6), LocalDate.now().minusDays(10)),
                new ClientData("Post Malone", "posty@hollywood.com", "posty",
                        Tier.SILVER, 6, new BigDecimal("9500.00"),
                        LocalDate.now().minusMonths(2), LocalDate.now().minusDays(3)),

                new ClientData("Elon Musk", "elon@tesla.com", "elon",
                        Tier.GOLD, 25, new BigDecimal("85000.00"),
                        LocalDate.now().minusYears(1), LocalDate.now().minusDays(1)),
                new ClientData("Mark Zuckerberg", "zuck@meta.com", "zuck",
                        Tier.GOLD, 18, new BigDecimal("62000.00"),
                        LocalDate.now().minusMonths(8), LocalDate.now().minusDays(4)),

                new ClientData("Jeff Bezos", "bezos@amazon.com", "bezos",
                        Tier.PLATINUM, 45, new BigDecimal("250000.00"),
                        LocalDate.now().minusYears(2), LocalDate.now()),
                new ClientData("Shrek Ogre", "shrek@swamp.com", "shrek",
                        Tier.PLATINUM, 38, new BigDecimal("180000.00"),
                        LocalDate.now().minusYears(1).minusMonths(6), LocalDate.now().minusDays(2)));

        for (ClientData data : clients) {
            createClientWithUser(data);
        }
    }

    private void createClientWithUser(ClientData data) {
        User user = User.builder()
                .username(data.username)
                .password(PasswordUtil.hash(data.username + "123"))
                .role(Role.CLIENT)
                .build();

        ClientStats stats = ClientStats.builder()
                .totalOrders(data.totalOrders)
                .totalSpent(data.totalSpent)
                .firstOrderDate(data.firstOrderDate)
                .lastOrderDate(data.lastOrderDate)
                .build();

        Client client = Client.builder()
                .name(data.name)
                .email(data.email)
                .loyaltyTier(data.tier)
                .stats(stats)
                .user(user)
                .build();

        clientRepository.save(client);

        log.debug("Created client: {} ({}) - Tier: {}", data.name, data.username, data.tier);
    }

    private record ClientData(
            String name,
            String email,
            String username,
            Tier tier,
            int totalOrders,
            BigDecimal totalSpent,
            LocalDate firstOrderDate,
            LocalDate lastOrderDate) {
    }
}
