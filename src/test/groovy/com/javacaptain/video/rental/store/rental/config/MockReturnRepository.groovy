package com.javacaptain.video.rental.store.rental.config


import com.javacaptain.video.rental.store.rental.api.ReturnId
import com.javacaptain.video.rental.store.rental.domain.Return
import com.javacaptain.video.rental.store.rental.domain.ReturnRepository

import java.util.concurrent.ConcurrentHashMap

class MockReturnRepository implements ReturnRepository {
    def db = new ConcurrentHashMap<ReturnId, Return>()

    @Override
    Return findById(ReturnId returnId) {
        return db.get(returnId)
    }

    @Override
    void save(Return rentalReturn) {
        db[rentalReturn.returnId()] = rentalReturn
    }
}
