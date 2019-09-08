package io.pivotal.dmfrey.workorder.adapter.out.integration;

import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.events.NodeAssigned;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
class DistributionBindingConfig {

    @Profile( "!cloud" )
    @EnableBinding( Node.NodeDistributionProcessor.class )
    static class Node {

        private final MessageChannel outputCloud;

        Node(
                @Qualifier( "workorder-events-distribution-cloud" ) final MessageChannel outputCloud
        ) {

            this.outputCloud = outputCloud;

        }

        @StreamListener( target = NodeDistributionProcessor.INPUT_STREAM )
        void process( Message<WorkorderDomainEvent> message ) {
            log.info( "process : message={}", message );

            if( !message.getHeaders().containsKey( "originationNode" ) ) {
                log.info( "process : message did not originate here" );

                return;
            }

            outputCloud.send(
                    MessageBuilder
                            .withPayload( message.getPayload() )
                            .setHeader( "workorderId", message.getPayload().workorderId() )
                            .build() );

        }

        interface NodeDistributionProcessor {

            String INPUT_STREAM = "workorder-events-distribution";
            String OUTPUT_CLOUD = "workorder-events-distribution-cloud";

            @Input( INPUT_STREAM )
            SubscribableChannel input();

            @Output( OUTPUT_CLOUD )
            MessageChannel outputCloud();

        }

    }

    @Profile( "cloud" )
    @EnableBinding( Cloud.CloudDistributionProcessor.class )
    static class Cloud {

        private final BinderAwareChannelResolver resolver;
        private final WorkorderEventsLookup workorderEventsLookup;

        Cloud(
                final BinderAwareChannelResolver resolver,
                final WorkorderEventsLookup workorderEventsLookup
        ) {

            this.resolver = resolver;
            this.workorderEventsLookup = workorderEventsLookup;

        }

        @StreamListener( target = CloudDistributionProcessor.INPUT_STREAM )
        void process( Message<WorkorderDomainEvent> message ) {
            log.info( "process : message={}", message );

            WorkorderDomainEvent event = message.getPayload();
            if( event instanceof NodeAssigned ) {
                log.info( "process : transfer initiated, sending all events" );

                final String target = String.format( "cloud.distribution-%s", ((NodeAssigned) event).targetNode() );
                log.info( "process : sending all to target={}", target );

                this.workorderEventsLookup.lookupByWorkorderId( event.workorderId() )
                    .forEach( e -> {
                        log.info( "process : event={}", e );

                        Message m = MessageBuilder
                                .withPayload( e )
                                .setHeader( "workorderId", e.workorderId() )
                                .build();

                        resolver.resolveDestination( target ).send( m );

                    });

                resolver.resolveDestination( target ).send(
                        MessageBuilder
                                .withPayload( event )
                                .setHeader("workorderId", event.workorderId() )
                                .build()
                );

                String currentTarget = String.format( "cloud.distribution-%s", ((NodeAssigned) event).currentNode() );
                log.info( "process : sending to target={}", currentTarget );

                resolver.resolveDestination( currentTarget ).send(
                        MessageBuilder
                                .withPayload( event )
                                .setHeader("workorderId", event.workorderId() )
                                .build()
                );

            } else {
                log.info( "process : sending event" );

                String target = String.format( "cloud.distribution-%s", event.node() );
                log.info( "process : sending to target={}", target );

                resolver.resolveDestination( target ).send(
                        MessageBuilder
                                .withPayload( event )
                                .setHeader("workorderId", event.workorderId() )
                                .build()
                );
            }

        }

        interface CloudDistributionProcessor {

            String INPUT_STREAM = "workorder-events-distribution";
            String OUTPUT_LOCAL = "workorder-events-distribution-local";
            String OUTPUT_NODE_17 = "workorder-events-distribution-node-17";
            String OUTPUT_NODE_34 = "workorder-events-distribution-node-34";

            @Input( INPUT_STREAM )
            SubscribableChannel input();

            @Output( OUTPUT_LOCAL )
            MessageChannel outputLocal();

            @Output( OUTPUT_NODE_17 )
            MessageChannel outputNode17();

            @Output( OUTPUT_NODE_34 )
            MessageChannel outputNode34();

        }

    }

}
