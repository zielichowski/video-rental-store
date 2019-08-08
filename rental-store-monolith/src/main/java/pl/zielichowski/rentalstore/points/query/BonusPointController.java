package pl.zielichowski.rentalstore.points.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RepositoryRestController
class BonusPointController {
    private final BonusPointsViewRepository bonusPointsViewRepository;

    @GetMapping("/points")
    public ResponseEntity<Resource<BonusPointsView>> getPoints(@RequestHeader("Api-key") String apiKey) {
        BonusPointsView bonusPointsView = bonusPointsViewRepository.findByUserId(apiKey);
        Link link = linkTo(methodOn(BonusPointController.class).getPoints(apiKey)).withSelfRel();
        Resource<BonusPointsView> bonusPointsViewResource = new Resource<>(bonusPointsView, link);
        return new ResponseEntity<>(bonusPointsViewResource, HttpStatus.OK);
    }

}
