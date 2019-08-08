package pl.zielichowski.rentalstore.rental.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zielichowski.rentalstore.common.api.rental.RentalItemStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
@Getter
class RentalItemView {

    @Id
    @GeneratedValue
    private Long id;

    private String itemId;

    @JsonIgnore
    private String userId;

    private int daysOfRental;

    @Setter
    private String surcharge;

    private String price;

    @Setter
    private RentalItemStatus rentalItemStatus;

}
