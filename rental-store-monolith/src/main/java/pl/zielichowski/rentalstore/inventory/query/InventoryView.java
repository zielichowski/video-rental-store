package pl.zielichowski.rentalstore.inventory.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InventoryView {
    @Id
    private String inventoryId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @RestResource(path = "movies", rel = "movies")
    private Set<MovieView> movieEntities = new HashSet<>();
}
