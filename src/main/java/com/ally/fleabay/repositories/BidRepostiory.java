package com.ally.fleabay.repositories;

import com.ally.fleabay.models.BidDatabaseEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface BidRepostiory  extends MongoRepository<BidDatabaseEntry, ObjectId> {
}
