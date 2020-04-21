package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.common.persistence.PersistenceAdapter;
import io.pivotal.dmfrey.workorder.application.out.GetAllWorkorderStatesPort;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import io.pivotal.dmfrey.workorder.application.out.WorkorderExistsPort;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@PersistenceAdapter
@RequiredArgsConstructor
class WorkorderPersistenceAdapter implements WorkorderExistsPort, GetWorkorderEventsPort, PersistWorkorderEventPort, GetAllWorkorderStatesPort {

    private final WorkorderEventsLookup lookup;
    private final AllWorkorderStates allWorkorderStates;
    private final WorkorderEventsMessageSender sender;

    @Override
    public List<WorkorderDomainEvent> getWorkorderEvents( final UUID workorderId ) {

        return lookup.lookupByWorkorderId( workorderId );
    }

    @Override
    public void save( final UUID workorderId, final List<WorkorderDomainEvent> events ) {

        sender.processEvents( workorderId, events );

    }

    @Override
    public boolean workorderExists( final UUID workorderId ) {

        return false;
    }

    @Override
    public List<Map<String, Object>> getAllWorkorderStates() {

        return allWorkorderStates.getAllWorkorderStates();
    }

}
