package io.pivotal.dmfrey.workorder.application.out;

import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;

import java.util.List;
import java.util.UUID;

public interface PersistWorkorderEventPort {

    void save( UUID workorderId, List<WorkorderDomainEvent> events );

}
