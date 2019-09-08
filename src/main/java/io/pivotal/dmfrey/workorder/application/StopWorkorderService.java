package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.workorder.application.in.StopWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.Workorder;

@Slf4j
@UseCase
@RequiredArgsConstructor
class StopWorkorderService implements StopWorkorderUseCase {

    private final GetWorkorderEventsPort getWorkorderEventsPort;
    private final PersistWorkorderEventPort persistWorkorderEventPort;
    private final NodeValidatorQuery nodeValidatorQuery;
    private final TimestampGenerator timestampGenerator;

    private final String user = "testUser";

    @Override
    public void execute( StopWorkorderCommand command ) {

        Workorder foundWorker = Workorder.createFrom( command.getWorkorderId(), getWorkorderEventsPort.getWorkorderEvents( command.getWorkorderId() ) );
        foundWorker.flushChanges();

        foundWorker.stopWorkorder( user, this.nodeValidatorQuery.execute( new NodeValidatorQuery.ValidateNodeCommand() ), timestampGenerator.generate() );
        this.persistWorkorderEventPort.save( foundWorker.id(), foundWorker.changes() );
        foundWorker.flushChanges();

    }

}
