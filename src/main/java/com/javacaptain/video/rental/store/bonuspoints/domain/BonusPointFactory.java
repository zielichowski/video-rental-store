package com.javacaptain.video.rental.store.bonuspoints.domain;

import com.javacaptain.video.rental.store.bonuspoints.api.BonusPoint;
import com.javacaptain.video.rental.store.common.MovieType;

class BonusPointFactory {
  /*
   * Simplified mapping movieType to bonus point
   */

  public static BonusPoint from(MovieType movieType) {
    return switch (movieType) {
      case NEW_RELEASE -> new BonusPoint(2);
      case REGULAR, OLD -> new BonusPoint(1);
    };
  }
}
