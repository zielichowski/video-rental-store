package com.javacaptain.video.rental.store.rental.web;

import com.javacaptain.video.rental.store.common.Money;

public record ReturnResponse(String returnId, Money surcharge) {}
