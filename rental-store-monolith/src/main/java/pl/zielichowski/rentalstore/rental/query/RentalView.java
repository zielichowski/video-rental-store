package pl.zielichowski.rentalstore.rental.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zielichowski.rentalstore.common.api.rental.PublicRentalStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class RentalView {

    @Id
    private String rentalId;
    @JsonIgnore
    private String userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(value = "items")
    private List<RentalItemView> rentalItemViews = new ArrayList<>();

    private String totalPrice;

    @Setter
    private String totalSurcharge;

    @Setter
    private PublicRentalStatus rentalStatus;
}
