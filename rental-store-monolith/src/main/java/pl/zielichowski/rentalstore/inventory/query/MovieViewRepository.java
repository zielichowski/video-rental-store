package pl.zielichowski.rentalstore.inventory.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "movies", path = "movies")
public interface MovieViewRepository extends PagingAndSortingRepository<MovieView, String> {
    @Override
    @RestResource(exported = false)
    <S extends MovieView> S save(S s);

    @Override
    @RestResource(exported = false)
    <S extends MovieView> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    @RestResource(exported = false)
    void deleteById(String s);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends MovieView> iterable);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
