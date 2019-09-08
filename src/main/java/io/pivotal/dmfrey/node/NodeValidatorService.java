package io.pivotal.dmfrey.node;

import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@UseCase
class NodeValidatorService implements NodeValidatorQuery {

    private final String node;

    public NodeValidatorService( @Value( "${node.current}" ) final String node ) {

        this.node = node;

    }

    @Override
    public String execute( final ValidateNodeCommand command ) {

        return node;
    }

}
