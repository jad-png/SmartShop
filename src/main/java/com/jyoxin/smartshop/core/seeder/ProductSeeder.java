package com.jyoxin.smartshop.core.seeder;

import com.jyoxin.smartshop.entity.Product;
import com.jyoxin.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Seeding products...");

            seedProducts();

            log.info("{} products seeded successfully", productRepository.count());
        } else {
            log.info("Products already exist, skipping...");
        }
    }

    private void seedProducts() {
        List<Product> products = List.of(
                Product.builder()
                        .name("iPhone 15 Pro Max 256GB")
                        .unitPrice(new BigDecimal("16499.00"))
                        .stock(50)
                        .build(),
                Product.builder()
                        .name("Samsung Galaxy S24 Ultra")
                        .unitPrice(new BigDecimal("14999.00"))
                        .stock(35)
                        .build(),
                Product.builder()
                        .name("Apple Watch Series 9 45mm")
                        .unitPrice(new BigDecimal("5499.00"))
                        .stock(30)
                        .build(),

                Product.builder()
                        .name("Dell XPS 15 i7 32GB RAM")
                        .unitPrice(new BigDecimal("18999.00"))
                        .stock(20)
                        .build(),
                Product.builder()
                        .name("ASUS ROG Strix G16 Gaming")
                        .unitPrice(new BigDecimal("15499.00"))
                        .stock(18)
                        .build(),
                Product.builder()
                        .name("LG UltraWide Monitor 34\"")
                        .unitPrice(new BigDecimal("5999.00"))
                        .stock(22)
                        .build(),
                Product.builder()
                        .name("Logitech MX Master 3S")
                        .unitPrice(new BigDecimal("1299.00"))
                        .stock(45)
                        .build(),
                Product.builder()
                        .name("Clavier Keychron K3 Pro")
                        .unitPrice(new BigDecimal("1599.00"))
                        .stock(35)
                        .build(),

                Product.builder()
                        .name("Dyson V15 Detect Aspirateur")
                        .unitPrice(new BigDecimal("8499.00"))
                        .stock(12)
                        .build(),
                Product.builder()
                        .name("Nespresso Vertuo Plus")
                        .unitPrice(new BigDecimal("2299.00"))
                        .stock(28)
                        .build(),

                Product.builder()
                        .name("Google Nest Hub Max")
                        .unitPrice(new BigDecimal("2799.00"))
                        .stock(20)
                        .build(),
                Product.builder()
                        .name("Ring Video Doorbell Pro 2")
                        .unitPrice(new BigDecimal("2499.00"))
                        .stock(25)
                        .build(),
                Product.builder()
                        .name("Philips Hue Starter Kit")
                        .unitPrice(new BigDecimal("1899.00"))
                        .stock(30)
                        .build(),

                Product.builder()
                        .name("PlayStation 5 Digital Edition")
                        .unitPrice(new BigDecimal("5499.00"))
                        .stock(10)
                        .build(),
                Product.builder()
                        .name("Xbox Series X")
                        .unitPrice(new BigDecimal("5999.00"))
                        .stock(12)
                        .build());

        productRepository.saveAll(products);
    }
}
