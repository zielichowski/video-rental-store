package com.javacaptain.video.rental.store.bonuspoints.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonusPointsRepository extends JpaRepository<BonusPoints, String> {
    Optional<BonusPoints> findByOwner(String owner);
}
