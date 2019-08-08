package pl.zielichowski.rentalstore.points.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import pl.zielichowski.rentalstore.common.api.rental.BonusPointsCalculatedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
class BonusPointsEventHandler {
    private final BonusPointsViewRepository bonusPointsViewRepository;


    @EventHandler
    public void on(BonusPointsCalculatedEvent event) {
        BonusPointsView bonusPointsView = bonusPointsViewRepository
                .findById(event.getUserId())
                .map(bp -> {
                    bp.addPoints(event.getBonusPoints());
                    return bp;
                })
                .orElse(new BonusPointsView(event.getUserId(), event.getBonusPoints()));

        bonusPointsViewRepository.save(bonusPointsView);
    }
}
