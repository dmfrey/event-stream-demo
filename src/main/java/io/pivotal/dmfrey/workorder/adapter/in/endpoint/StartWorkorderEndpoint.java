package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.application.in.StartWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.in.StartWorkorderUseCase.StartWorkorderCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class StartWorkorderEndpoint {

    private final StartWorkorderUseCase useCase;

    @PutMapping( "/workorders/{workorderId}/start" )
    public ResponseEntity startWorkorder( @PathVariable( "workorderId" ) UUID workorderId ) {

        this.useCase.execute( new StartWorkorderCommand( workorderId ) );

        return ResponseEntity
                .accepted()
                .build();
    }
}
