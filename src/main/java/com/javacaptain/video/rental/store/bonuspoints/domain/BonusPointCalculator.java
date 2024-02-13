package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPoint;
import com.javacaptain.video.rental.store.common.MovieId;
import java.util.List;

public interface BonusPointCalculator {
    BonusPoint calculate(List<MovieId> movies);
}
