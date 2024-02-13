package com.javacaptain.video.rental.store.pricing.domain

import com.javacaptain.video.rental.store.pricing.infrastructure.JpaPricingMovieRepository
import com.javacaptain.video.rental.store.pricing.infrastructure.PricingMovieEntity

import java.util.concurrent.ConcurrentHashMap

class MockPricingMovieRepository implements JpaPricingMovieRepository {
    def db = new ConcurrentHashMap<String, PricingMovieEntity>()

    @Override
    <S extends PricingMovieEntity> S save(S entity) {
        def movie = entity as PricingMovieEntity
        return db[movie.movieId] = movie
    }

    <S extends PricingMovieEntity> Iterable<S> saveAll(Iterable<S> entities) {
        return  null
    }


    @Override
    Optional<PricingMovieEntity> findById(String s) {
        return Optional.of(db.get(s))
    }

    @Override
    boolean existsById(String s) {
        return false
    }

    @Override
    Iterable<PricingMovieEntity> findAll() {
        return null
    }

    @Override
    Iterable<PricingMovieEntity> findAllById(Iterable<String> strings) {
        return null
    }

    @Override
    long count() {
        return 0
    }

    @Override
    void deleteById(String s) {

    }

    @Override
    void delete(PricingMovieEntity entity) {

    }

    @Override
    void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    void deleteAll(Iterable<? extends PricingMovieEntity> entities) {

    }

    @Override
    void deleteAll() {

    }
}
