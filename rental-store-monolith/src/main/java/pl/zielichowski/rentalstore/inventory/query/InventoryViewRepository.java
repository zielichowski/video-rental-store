package pl.zielichowski.rentalstore.inventory.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "inventories", path = "inventories")
public interface InventoryViewRepository extends PagingAndSortingRepository<InventoryView, String> {
}
