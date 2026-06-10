package com.example.modulith.vet;

import com.example.modulith.adoptions.DogAdoptedEvent;
import com.example.modulith.validation.Validation;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
class Vet {

    private final Validation validation;

    Vet(Validation validation) {
        this.validation = validation;
    }

    @ApplicationModuleListener
    void checkup(DogAdoptedEvent dogId) throws Exception {
        Thread.sleep(5 * 1000);
        IO.println("scheduled dog: " + dogId);
    }
}
