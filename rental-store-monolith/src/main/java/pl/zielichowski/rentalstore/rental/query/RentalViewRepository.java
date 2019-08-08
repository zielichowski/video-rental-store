package pl.zielichowski.rentalstore.rental.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "/rentals", path = "/rentals")
public interface RentalViewRepository extends PagingAndSortingRepository<RentalView, String> {
    @RestResource(exported = false)
    Page<RentalView> findAllByUserId(String userId, Pageable pageable);

    @RestResource(exported = false)
    RentalView findByUserIdAndRentalId(String userId, String rentalId);

    @Override
    @RestResource(exported = false)
    <S extends RentalView> S save(S s);

    @Override
    @RestResource(exported = false)
    <S extends RentalView> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    @RestResource(exported = false)
    void deleteById(String s);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    Page<RentalView> findAll(Pageable pageable);

    @Override
    @RestResource(exported = false)
    Optional<RentalView> findById(@Param("id") String id);
}
