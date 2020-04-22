package io.pivotal.dmfrey.workorder.adapter.out.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.WorkorderExceptions.WorkorderNotFoundException;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class WorkorderEventsLookup {

    private final InteractiveQueryService interactiveQueryService;

    @Retryable(
            value = WorkorderNotFoundException.class,
            maxAttempts = 5,
            backoff = @Backoff( delay = 2000 )
    )
    List<WorkorderDomainEvent> lookupByWorkorderId( final UUID workorderId ) {

        final ReadOnlyKeyValueStore<UUID, List<WorkorderDomainEvent>> eventStore =
                interactiveQueryService.getQueryableStore("workorder-events-by-id",
                        QueryableStoreTypes.keyValueStore() );

        List<WorkorderDomainEvent> events = eventStore.get( workorderId );
        if( null == events ) {

            throw new WorkorderNotFoundException( workorderId );
        }

        return events;
    }

}
