package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.TransferWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.in.TransferWorkorderUseCase.TransferWorkorderCommand;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class TransferWorkorderEndpoint {

    private final TransferWorkorderUseCase useCase;

    @PutMapping( "/workorders/{workorderId}/transfer" )
    public ResponseEntity transferWorkorder( @PathVariable( "workorderId" ) UUID workorderId, @RequestBody TransferRequest transferRequest ) {

        this.useCase.execute( new TransferWorkorderCommand( workorderId, transferRequest.getTargetNode() ) );

        return ResponseEntity
                .accepted()
                .build();
    }

    @Value
    static class TransferRequest {

        String targetNode;

        @JsonCreator
        TransferRequest(
                @JsonProperty( "targetNode" ) final String targetNode
        ) {

            this.targetNode = targetNode;

        }
    }

}
