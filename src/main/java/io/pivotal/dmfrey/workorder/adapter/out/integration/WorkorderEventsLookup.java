package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;

import java.util.List;
import java.util.UUID;

public interface WorkorderEventsLookup {

    List<WorkorderDomainEvent> lookupByWorkorderId( final UUID workorderId );

}
