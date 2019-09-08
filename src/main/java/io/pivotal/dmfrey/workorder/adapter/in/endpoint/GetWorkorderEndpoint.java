package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.GetWorkorderQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class GetWorkorderEndpoint {

    private final GetWorkorderQuery useCase;

    @GetMapping( "/workorders/{workorderId}" )
    public ResponseEntity createWorkorder( @PathVariable( "workorderId" ) UUID workorderId ) {

        Map<String, Object> found = this.useCase.execute( new GetWorkorderQuery.GetWorkorderCommand( workorderId ) );

        return ResponseEntity
                .ok( found );
    }

}
