package pl.zielichowski.rentalstore.rental.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false, collectionResourceRel = "/items")
public interface RentalItemViewRepository extends JpaRepository<RentalItemView, String> {
    Optional<RentalItemView> findByItemId(String id);
}
