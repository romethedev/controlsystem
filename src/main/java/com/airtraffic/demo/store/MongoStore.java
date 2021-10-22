package com.airtraffic.demo.store;

import com.airtraffic.demo.exception.OptimisticLockException;
import com.airtraffic.demo.service.ControlFilter;
import com.airtraffic.demo.service.ControlAircraft;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mongo")
public class MongoStore implements ControlStore {

    @Autowired
    private Datastore store;

    @Override
    public void drop() {
        store.getCollection(ControlAircraft.class).drop();
    }

    @Override
    public List<ControlAircraft> list(ControlFilter filter) {
        final Query<ControlAircraft> query = store.createQuery(ControlAircraft.class);
        if (filter.getSize() != null) {
            query.field("size").equal(filter.getSize());
        }
        if (filter.getType() != null) {
            query.field("type").equal(filter.getType());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.field("label").containsIgnoreCase(filter.getSearch().trim());
        }
        if (filter.getLastId() != null) {
            final ControlAircraft lastRecord = store.get(ControlAircraft.class, filter.getLastId());
            if (lastRecord != null) {
                query.or(
                    query.criteria("type").greaterThan(lastRecord.getType()),
                    query.and(query.criteria("type").equal(lastRecord.getType()), query.criteria("size").greaterThan(lastRecord.getSize())),
                    query.and(query.criteria("type").equal(lastRecord.getType()), query.criteria("size").equal(lastRecord.getSize()), query.criteria("timestamp").greaterThan(lastRecord.getTimestamp())),
                    query.and(query.criteria("type").equal(lastRecord.getType()), query.criteria("size").equal(lastRecord.getSize()), query.criteria("timestamp").equal(lastRecord.getTimestamp()), query.criteria("id").greaterThan(lastRecord.getId())));
            }
        }
        query.order("type,size,timestamp,id");
        final FindOptions options = new FindOptions().limit(filter.getLimit());
        return query.asList(options);
    }

    @Override
    public ControlAircraft create(ControlAircraft aircraft) {
        final Key<ControlAircraft> key = store.save(aircraft);
        aircraft.setId((ObjectId) key.getId());
        return aircraft;
    }

    @Override
    public ControlAircraft remove(ObjectId id) {
        final Query<ControlAircraft> query = store.createQuery(ControlAircraft.class).field("id").equal(id);
        final ControlAircraft deleted = store.findAndDelete(query);
        if (deleted == null) {
            throw new OptimisticLockException(String.format("Plane with %s not found", id));
        }
        return deleted;
    }
}