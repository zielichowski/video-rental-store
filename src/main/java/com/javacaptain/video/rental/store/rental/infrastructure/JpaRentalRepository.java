package com.javacaptain.video.rental.store.rental.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

/* We use different model for database- RentalEntity */
public interface JpaRentalRepository extends JpaRepository<RentalEntity, String> {}
