package com.javacaptain.video.rental.store.rental.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReturnRepository extends JpaRepository<ReturnEntity, String> {}
