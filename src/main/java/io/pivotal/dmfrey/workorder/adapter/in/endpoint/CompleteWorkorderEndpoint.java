package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.CompleteWorkorderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class CompleteWorkorderEndpoint {

    private final CompleteWorkorderUseCase useCase;

    @PutMapping( "/workorders/{workorderId}/complete" )
    public ResponseEntity completeWorkorder( @PathVariable( "workorderId" ) UUID workorderId ) {

        this.useCase.execute( new CompleteWorkorderUseCase.CompleteWorkorderCommand( workorderId ) );

        return ResponseEntity
                .accepted()
                .build();
    }
}
