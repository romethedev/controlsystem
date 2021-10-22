package com.airtraffic.demo.web;

import com.airtraffic.demo.service.ControlFilter;
import com.airtraffic.demo.service.ControlAircraft;
import com.airtraffic.demo.service.ControlService;
import com.airtraffic.demo.service.ListResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class SystemController {

    @Autowired
    private ControlService service;

    @RequestMapping(value = "/", method = GET)
    public ListResponse<ControlAircraft> list(@RequestParam(required = false) String search,
                                      @RequestParam(required = false) ObjectId lastId,
                                      @RequestParam(required = false) Integer limit,
                                      @RequestParam(required = false) ControlAircraft.Type type,
                                      @RequestParam(required = false) ControlAircraft.Size size) {
        final ControlFilter filter = new ControlFilter();
        filter.setSearch(search);
        filter.setLastId(lastId);
        if (limit !=null) filter.setLimit(limit);
        filter.setType(type);
        filter.setSize(size);
        return service.list(filter);
    }

    @RequestMapping(value = "/", method = PUT)
    public ControlAircraft push(@RequestBody ControlAircraft aircraft) {
        return service.push(aircraft);
    }

    @RequestMapping(value = "/", method = DELETE)
    public ControlAircraft pop() {
        return service.pop();
    }

}