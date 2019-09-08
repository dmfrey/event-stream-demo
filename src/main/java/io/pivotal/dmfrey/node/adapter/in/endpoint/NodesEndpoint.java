package io.pivotal.dmfrey.node.adapter.in.endpoint;

import io.pivotal.dmfrey.common.endpoint.EndpointAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Slf4j
@EndpointAdapter
public class NodesEndpoint {

    private final String currentNode;
    private final List<String> availableNodes;


    public NodesEndpoint(
            @Value( "${node.current}" ) final String currentNode,
            @Value( "${node.available}" ) final String[] availableNodes
    ) {

        this.currentNode = currentNode;
        this.availableNodes = asList( availableNodes );

    }

    @GetMapping( "/nodes" )
    public ResponseEntity nodeDetails() {

        Map<String, Object> nodeDetails = new HashMap<>();
        nodeDetails.put( "currentNode", currentNode );
        nodeDetails.put( "availableNodes", availableNodes );

        return ResponseEntity
                .ok( nodeDetails );
    }

}
