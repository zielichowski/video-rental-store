package com.javacaptain.video.rental.store.rental.web;


import java.util.List;

public record CreateRentalRequest(List<RentalDto> rentalItems, String clientId) {}
