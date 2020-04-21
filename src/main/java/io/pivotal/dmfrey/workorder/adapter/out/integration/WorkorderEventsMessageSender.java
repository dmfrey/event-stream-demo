package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
class WorkorderEventsMessageSender {

    private final StreamBridge streamBridge;
    private final String node;

    WorkorderEventsMessageSender(
            final StreamBridge streamBridge,
            @Value( "${node.current}" ) final String node
    ) {

        this.streamBridge = streamBridge;
        this.node = node;

    }

    void processEvents( final UUID workorderId, List<WorkorderDomainEvent> events) {

        events.forEach( event -> {
            log.info( "processEvents : event={}", event );
            this.streamBridge
                    .send( "workorder-events-out-0",
                            MessageBuilder
                                    .withPayload( event )
                                    .setHeader( "workorderId", workorderId )
                                    .setHeader( "originationNode", node )
                                    .build()
                    );

        });

    }

}
