package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.common.useCase.UuidGenerator;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery.ValidateNodeCommand;
import io.pivotal.dmfrey.workorder.application.in.CreateWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import io.pivotal.dmfrey.workorder.domain.Workorder;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
    public Map<String, Object> execute( CreateWorkorderCommand command ) {

        Workorder workorder = new Workorder( this.uuidGenerator.generate() );

        String node = this.nodeValidatorQuery.execute( new ValidateNodeCommand() );

        workorder.updateName( command.getTitle(), user, node, this.timestampGenerator.generate() );

        if( null != command.getTargetNode() ) {

            workorder.assignNode( command.getTargetNode(), user, node, this.timestampGenerator.generate() );

        }

        this.persistWorkorderEventPort.save( workorder.id(), workorder.changes() );
        workorder.flushChanges();

        return workorder.getWorkorderView();
    }

}
