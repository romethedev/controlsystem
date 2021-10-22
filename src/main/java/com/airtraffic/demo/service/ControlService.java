package com.airtraffic.demo.service;

import com.airtraffic.demo.exception.BadRequest;
import com.airtraffic.demo.exception.OptimisticLockException;
import com.airtraffic.demo.exception.QueueIsEmpty;
import com.airtraffic.demo.store.ControlStore;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Air Controller Service
 */
@Service
public class ControlService {

    private static final Logger log = LoggerFactory.getLogger(ControlService.class);

    private static final int MAX_PAGE_SIZE = 50;

    private static final int MAX_TRY = 10;

    @Autowired
    private ControlStore store;

    @Autowired
    private Validator validator;

    public ListResponse<ControlAircraft> list(ControlFilter filter) {
        log.debug("list {}", filter);

        if (filter.getLimit() < 1 || filter.getLimit() > MAX_PAGE_SIZE) {
            filter.setLimit(MAX_PAGE_SIZE);
        }
        filter.setLimit(filter.getLimit() + 1);
        final List<ControlAircraft> items = store.list(filter);
        final boolean hasMore = items.size() == filter.getLimit();
        if (hasMore) {
            items.remove(items.size() - 1);
        }
        filter.setLimit(filter.getLimit() - 1);

        final ListResponse<ControlAircraft> response = new ListResponse<>();
        response.setItems(items);
        response.setHasMore(hasMore);
        response.setFilter(filter);
        log.debug("result: {}", response);
        return response;

    }

    public ControlAircraft push(ControlAircraft aircraft) {
        log.debug("push: {}", aircraft);
        aircraft.setTimestamp(System.currentTimeMillis());
        if (aircraft.getLabel() == null) {
            aircraft.setLabel(aircraft.getSize() + " " + aircraft.getType() + " " + aircraft.getTimestamp());
        }
        final Set<ConstraintViolation<ControlAircraft>> validationResult = validator.validate(aircraft);
        if (CollectionUtils.isNotEmpty(validationResult)) {
            throw new BadRequest(new ConstraintViolationException(validationResult));
        }
        final ControlAircraft result = store.create(aircraft);
        log.debug("created: {}", result);
        return result;
    }

    public ControlAircraft pop() {
        log.debug("pop");
        return optimisticOperation(() -> {
            final ControlFilter request = new ControlFilter();
            request.setLimit(1);

            final List<ControlAircraft> items = store.list(request);
            if (CollectionUtils.isEmpty(items)) {
                throw new QueueIsEmpty("Queue is empty");
            }

            final ControlAircraft head = items.get(0);
            store.remove(head.getId());

            log.debug("pop result {}", head);
            return head;
        });
    }

    private <T> T optimisticOperation(Supplier<T> callable) {
        for (int i = 1; i < MAX_TRY; i++) {
            try {
                return callable.get();
            } catch (OptimisticLockException e) {
                log.debug("Attempt {} failed", i);
            }
        }
        //Give it last try
        return callable.get();
    }

}