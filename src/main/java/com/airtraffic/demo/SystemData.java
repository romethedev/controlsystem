package com.airtraffic.demo;

import com.airtraffic.demo.service.ControlFilter;
import com.airtraffic.demo.service.ControlAircraft;
import com.airtraffic.demo.service.ControlService;
import com.airtraffic.demo.store.ControlStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class SystemData {

    private static final Logger log = LoggerFactory.getLogger(SystemData.class);

    private static final int QUEUE_SIZE = 10;

    @Autowired
    private ControlService service;

    @Autowired
    private ControlStore store;

    @PostConstruct
    public void generate() {
        if (CollectionUtils.isEmpty(store.list(new ControlFilter()))) {
            log.info("DB is empty - generating test data");
        }
        final Random random = new Random();
        for (int i = 0; i < QUEUE_SIZE; i++) {
            final ControlAircraft aircraft = new ControlAircraft();
            aircraft.setType(ControlAircraft.Type.values()[random.nextInt(ControlAircraft.Type.values().length)]);
            aircraft.setSize(ControlAircraft.Size.values()[random.nextInt(ControlAircraft.Size.values().length)]);
            aircraft.setLabel(String.format("%s %s %d", aircraft.getSize(), aircraft.getType(), i));
            service.push(aircraft);
        }
        log.info("{} ControlAircrafts generated", QUEUE_SIZE);
    }
}