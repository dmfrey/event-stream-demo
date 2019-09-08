package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.TransferWorkorderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class TransferWorkorderEndpoint {

    private final TransferWorkorderUseCase useCase;

    @PutMapping( "/workorders/{workorderId}/transfer" )
    public ResponseEntity openWorkorder( @PathVariable( "workorderId" ) UUID workorderId, @RequestParam( "targetNode" ) String targetNode ) {

        this.useCase.execute( new TransferWorkorderUseCase.TransferWorkorderCommand( workorderId, targetNode ) );

        return ResponseEntity
                .accepted()
                .build();
    }
}
