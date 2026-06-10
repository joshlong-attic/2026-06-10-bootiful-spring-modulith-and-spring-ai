package com.example.modulith.adoptions;

import com.example.modulith.validation.Validation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
class AdoptionsController {

    private final Validation validation;
    private final DogsService service;

    AdoptionsController(Validation validation, DogsService service) {
        this.validation = validation;
        this.service = service;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.service.adopt(dogId, owner);
    }
}

@Service
@Transactional
class DogsService {

    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    DogsService(DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    void adopt(int dogId, String owner) {
        this.repository.findById(dogId).ifPresent(dog -> {
            var updated = this.repository.save(
                    new Dog(dog.id(), dog.name(), owner, dog.description()));
            this.applicationEventPublisher.publishEvent(new DogAdoptedEvent(dogId));
            IO.println("adopted: " + updated);
        });
    }
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String owner, String description) {
}