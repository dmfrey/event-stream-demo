package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.CreateWorkorderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class CreateWorkorderEndpoint {

    private final CreateWorkorderUseCase useCase;

    @PostMapping( path = "/workorders" )
    public ResponseEntity createWorkorder( @RequestBody Map<String, String> parameters, UriComponentsBuilder builder ) {

        Map<String, Object> created = this.useCase.execute( new CreateWorkorderUseCase.CreateWorkorderCommand( parameters.get( "title" ), parameters.getOrDefault( "targetNode", null ) ) );

        URI location = builder.path( "/workorders/{workorderId}" ).buildAndExpand( created.get( "workorderId" ) ).toUri();

        return ResponseEntity
                .created( location )
                .build();
    }

}
