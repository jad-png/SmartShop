package com.jyoxin.smartshop.core.seeder;

import com.jyoxin.smartshop.entity.PromoCode;
import com.jyoxin.smartshop.repository.PromoCodeRepository;
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
@Order(4)
public class PromoCodeSeeder implements CommandLineRunner {

    private final PromoCodeRepository promoCodeRepository;

    @Override
    public void run(String... args) {
        if (promoCodeRepository.count() == 0) {
            log.info("Seeding promo codes...");

            seedPromoCodes();

            log.info("{} promo codes seeded successfully", promoCodeRepository.count());
        } else {
            log.info("ℹPromo codes already exist, skipping...");
        }
    }

    private void seedPromoCodes() {
        List<PromoCode> promoCodes = List.of(
                PromoCode.builder()
                        .code("PROMO-0000")
                        .percentage(new BigDecimal("10"))
                        .active(true)
                        .maxUsage(100)
                        .currentUsage(23)
                        .build(),

                PromoCode.builder()
                        .code("PROMO-BIGG")
                        .percentage(new BigDecimal("99"))
                        .active(true)
                        .maxUsage(50)
                        .currentUsage(12)
                        .build(),

                PromoCode.builder()
                        .code("PROMO-NONE")
                        .percentage(new BigDecimal("50"))
                        .active(false)
                        .maxUsage(10)
                        .currentUsage(10)
                        .build(),

                PromoCode.builder()
                        .code("PROMO-TEST")
                        .percentage(new BigDecimal("10"))
                        .active(true)
                        .maxUsage(9999)
                        .currentUsage(0)
                        .build());

        promoCodeRepository.saveAll(promoCodes);
    }
}
