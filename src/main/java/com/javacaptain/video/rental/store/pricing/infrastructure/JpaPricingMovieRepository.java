package com.javacaptain.video.rental.store.pricing.infrastructure;

import org.springframework.data.repository.CrudRepository;

public interface JpaPricingMovieRepository extends CrudRepository<PricingMovieEntity, String> {}
