package com.javacaptain.video.rental.store.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovieRepository extends JpaRepository<InventoryMovie, String> {}
