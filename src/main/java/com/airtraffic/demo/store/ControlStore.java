package com.airtraffic.demo.store;

import com.airtraffic.demo.service.ControlFilter;
import com.airtraffic.demo.service.ControlAircraft;
import org.bson.types.ObjectId;

import java.util.List;

public interface ControlStore {

    void drop();
    List<ControlAircraft> list(ControlFilter filter);
    ControlAircraft create(ControlAircraft aircraft);
    ControlAircraft remove(ObjectId id);

}