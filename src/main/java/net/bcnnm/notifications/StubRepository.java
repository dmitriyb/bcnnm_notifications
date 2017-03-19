package net.bcnnm.notifications;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StubRepository extends MongoRepository<Stub, String> {
    public Stub findByName(String name);
}
