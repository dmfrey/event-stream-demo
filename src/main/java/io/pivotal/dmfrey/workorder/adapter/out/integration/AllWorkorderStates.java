package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.workorder.domain.WorkorderExceptions;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.Workorder;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
class AllWorkorderStates {

    private final InteractiveQueryService interactiveQueryService;
    private final String node;

    AllWorkorderStates(
            final InteractiveQueryService interactiveQueryService,
            @Value( "${node.current}" ) final String node
    ) {

        this.interactiveQueryService = interactiveQueryService;
        this.node = node;

    }

    @Retryable(
            value = WorkorderExceptions.WorkorderNotFoundException.class,
            maxAttempts = 5,
            backoff = @Backoff( delay = 2000 )
    )
    List<Map<String, Object>> getAllWorkorderStates() {

        final ReadOnlyKeyValueStore<UUID, List<WorkorderDomainEvent>> eventStore =
                interactiveQueryService.getQueryableStore("workorder-events-by-id",
                        QueryableStoreTypes.keyValueStore() );

        List<Map<String, Object>> workorderStates = new ArrayList<>();
        eventStore.all()
                .forEachRemaining( kv -> {

                    Workorder wo = Workorder.createFrom( kv.key, kv.value );

                    if( "cloud".equals( node ) || wo.assigned().equals( node ) ) {

                        Map<String, Object> workorderState = new HashMap<>();
                        workorderState.put( "workorderId", wo.id() );
                        workorderState.put( "state", wo.state() );
                        workorderState.put( "title", wo.title() );

                        workorderStates.add( workorderState );

                    }

                });

        return workorderStates;
    }

}
