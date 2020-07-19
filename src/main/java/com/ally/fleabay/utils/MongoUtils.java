package com.ally.fleabay.utils;

import com.ally.fleabay.exceptions.InvalidObjectIdException;
import org.bson.types.ObjectId;

public class MongoUtils {

    public static ObjectId getObjectIdFromString(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException ex){
            throw new InvalidObjectIdException();
        }
        return objectId;
    }
}
