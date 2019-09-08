package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.common.useCase.UuidGenerator;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.workorder.application.in.CreateWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import lombok.RequiredArgsConstructor;
import io.pivotal.dmfrey.workorder.domain.Workorder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
class CreateWorkorderService implements CreateWorkorderUseCase {

    private final PersistWorkorderEventPort persistWorkorderEventPort;
    private final NodeValidatorQuery nodeValidatorQuery;
    private final UuidGenerator uuidGenerator;
    private final TimestampGenerator timestampGenerator;

    private final String user = "testUser";

    @Override
    @Transactional
    public UUID execute( CreateWorkorderCommand command ) {

        Workorder workorder = new Workorder( this.uuidGenerator.generate() );

        String node = this.nodeValidatorQuery.execute( new NodeValidatorQuery.ValidateNodeCommand() );

        workorder.updateName( command.getTitle(), user, node, this.timestampGenerator.generate() );

        if( null != command.getTargetNode() ) {

            workorder.assignNode( command.getTargetNode(), user, node, this.timestampGenerator.generate() );

        }

        this.persistWorkorderEventPort.save( workorder.id(), workorder.changes() );
        workorder.flushChanges();

        return workorder.id();
    }

}
