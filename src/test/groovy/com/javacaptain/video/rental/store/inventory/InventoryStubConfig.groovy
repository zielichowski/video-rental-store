package com.javacaptain.video.rental.store.inventory

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener
import com.javacaptain.video.rental.store.pricing.domain.PricingEventListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class InventoryStubConfig {
    @Bean("stubBonusPointListener")
    @Primary
    BonusPointEventListener bonusPointEventListener() {
        return new BonusPointTestListener()
    }

    @Bean("stubPricingListener")
    @Primary
    PricingEventListener pricingEventListener() {
        return new PricingTestListener()
    }
}
