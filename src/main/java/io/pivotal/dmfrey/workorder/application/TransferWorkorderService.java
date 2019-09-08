package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.workorder.application.in.TransferWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.Workorder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class TransferWorkorderService implements TransferWorkorderUseCase {

    private final GetWorkorderEventsPort getWorkorderEventsPort;
    private final PersistWorkorderEventPort persistWorkorderEventPort;
    private final NodeValidatorQuery nodeValidatorQuery;
    private final TimestampGenerator timestampGenerator;

    private final String user = "testUser";

    @Override
    @Transactional
    public void execute( final TransferWorkorderCommand command ) {

        Workorder foundWorker = Workorder.createFrom( command.getWorkorderId(), getWorkorderEventsPort.getWorkorderEvents( command.getWorkorderId() ) );
        foundWorker.flushChanges();

        foundWorker.assignNode( command.getTargetNode(), user, this.nodeValidatorQuery.execute( new NodeValidatorQuery.ValidateNodeCommand() ), this.timestampGenerator.generate() );

        this.persistWorkorderEventPort.save( foundWorker.id(), foundWorker.changes() );
        foundWorker.flushChanges();

    }

}
