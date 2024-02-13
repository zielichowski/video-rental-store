package com.javacaptain.video.rental.store.rental.config

import com.javacaptain.video.rental.store.bonuspoints.domain.BonusPointEventListener
import com.javacaptain.video.rental.store.rental.domain.PricingAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class StubConfig {
    @Bean("stubPricing")
    @Primary
    PricingAdapter pricingAdapter() {
        return new MockPricingAdapter()
    }

    @Bean("stubBonusPointListener")
    @Primary
    BonusPointEventListener bonusPointEventListener() {
        return new BonusPointTestListener()
    }

}
