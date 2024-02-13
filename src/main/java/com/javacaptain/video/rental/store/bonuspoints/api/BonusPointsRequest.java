package com.javacaptain.video.rental.store.bonuspoints.api;

import com.javacaptain.video.rental.store.common.MovieId;
import com.javacaptain.video.rental.store.rental.api.ClientId;
import java.util.List;

public record BonusPointsRequest(
        ClientId clientId,
        List<MovieId> movies
) {}
