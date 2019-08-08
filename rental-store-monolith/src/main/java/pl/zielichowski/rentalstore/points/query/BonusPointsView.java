package pl.zielichowski.rentalstore.points.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BonusPointsView {

    @Id
    private String userId;

    private int bonusPoints;

    void addPoints(int bonusPoints) {
        this.bonusPoints += bonusPoints;
    }
}
