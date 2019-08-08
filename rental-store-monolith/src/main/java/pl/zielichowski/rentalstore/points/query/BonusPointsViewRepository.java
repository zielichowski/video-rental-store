package pl.zielichowski.rentalstore.points.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface BonusPointsViewRepository extends JpaRepository<BonusPointsView, String> {
    BonusPointsView findByUserId(String userId);
}
