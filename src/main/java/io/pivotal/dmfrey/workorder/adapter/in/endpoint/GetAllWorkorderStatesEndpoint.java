package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import io.pivotal.dmfrey.workorder.application.in.GetAllWorkorderStatesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@EndpointAdapter
@RequiredArgsConstructor
public class GetAllWorkorderStatesEndpoint {

    private final GetAllWorkorderStatesQuery useCase;

    @GetMapping( "/workorders" )
    public ResponseEntity allWorkorderStates() {

        return ResponseEntity
                .ok( this.useCase.execute( new GetAllWorkorderStatesQuery.GetAllWorkorderStatesCommand() ) );
    }

}
