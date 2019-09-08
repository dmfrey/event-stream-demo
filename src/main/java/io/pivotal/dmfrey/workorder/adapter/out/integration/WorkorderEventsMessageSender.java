package io.pivotal.dmfrey.workorder.adapter.out.integration;

import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
class WorkorderEventsMessageSender {

    private final MessageChannel output;
    private final String node;

    WorkorderEventsMessageSender(
            @Qualifier( "workorder-events-output" ) final MessageChannel output,
            @Value( "${node.current}" ) final String node
    ) {

        this.output = output;
        this.node = node;

    }

    @SendTo( WorkorderBindingConfig.WorkorderEventsProcessor.OUTPUT )
    void processEvents( final UUID workorderId, List<WorkorderDomainEvent> events) {

        events.forEach( event -> {
            log.info( "processEvents : event={}", event );
            output.send(
                    MessageBuilder
                            .withPayload(event)
                            .setHeader("workorderId", workorderId)
                            .setHeader("originationNode", node)
                            .build()
            );
        });

    }

}
