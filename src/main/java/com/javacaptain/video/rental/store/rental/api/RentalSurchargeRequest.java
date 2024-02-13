package com.javacaptain.video.rental.store.rental.api;

import com.javacaptain.video.rental.store.pricing.api.SurchargeRequest;
import java.util.List;

public record RentalSurchargeRequest(List<RentalItemSurchargeRequest> surchargeRequests) {
    public List<SurchargeRequest> toSurchargeRequest() {
        return this.surchargeRequests.stream().map(sr ->
                new SurchargeRequest(sr.movieId(),sr.extraRentalDays())).toList();
    }
}
