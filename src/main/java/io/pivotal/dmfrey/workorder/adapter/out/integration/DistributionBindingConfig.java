package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.workorder.domain.events.NodeAssigned;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
class DistributionBindingConfig {

    @Component
    @Profile( "!cloud" )
    @RequiredArgsConstructor
    static class Node {

        private final StreamBridge streamBridge;

        @Bean
        public Consumer<KStream<UUID, WorkorderDomainEvent>> workorderEventsDistribution() {

            return events -> events
                    .process( () -> new Processor<>() {

                        ProcessorContext context;

                        @Override
                        public void init( ProcessorContext context ) {

                            this.context = context;

                        }

                        @Override
                        public void process( UUID key, WorkorderDomainEvent event ) {
                            log.info( "workorderEventsDistribution.processor.process : key={} event={}", key, event );

                            final Headers headers = this.context.headers();
                            final Iterable<Header> originationNodes = headers.headers( "originationNode" );
                            if( originationNodes != null ) {
                                log.info( "workorderEventsDistribution.processor.process : message did not originate here" );

                                return;
                            }

                            log.info( "workorderEventsDistribution.processor.process : sending event to cloud distribution" );
                            streamBridge.send( "workorder-events-distribution-cloud-out-0",
                                    MessageBuilder
                                            .withPayload( event )
                                            .setHeader( "workorderId", event.workorderId() )
                                            .build()
                            );

                        }

                        @Override
                        public void close() {

                        }
                    });

        }

    }

    @Component
    @Profile( "cloud" )
    @RequiredArgsConstructor
    static class Cloud {

        private final StreamBridge streamBridge;
        private final WorkorderEventsLookup workorderEventsLookup;

        @Bean
        public Consumer<KStream<Object, WorkorderDomainEvent>> workorderEventsDistribution() {

            return events -> events
                    .foreach( (key,event) -> {
                        log.info( "workorderEventsDistribution : event={}", event );

                        if( event instanceof NodeAssigned) {
                            log.info( "workorderEventsDistribution : transfer initiated, sending all events" );

                            final String target = String.format( "workorder-events-distribution-%s-out-0", ( (NodeAssigned) event).targetNode() );
                            log.info( "workorderEventsDistribution : sending all to target={}", target );

                            this.workorderEventsLookup.lookupByWorkorderId( event.workorderId() )
                                    .forEach( e -> {
                                        log.info( "workorderEventsDistribution : event={}", e );

                                        this.streamBridge.send( target,
                                                MessageBuilder
                                                        .withPayload( e )
                                                        .setHeader( "workorderId", e.workorderId() )
                                                        .build()
                                        );

                                    });

                            this.streamBridge.send( target,
                                    MessageBuilder
                                            .withPayload( event )
                                            .setHeader( "workorderId", event.workorderId() )
                                            .build()
                            );

                            String currentTarget = String.format( "workorder-events-distribution-%s-out-0", ( (NodeAssigned) event ).currentNode() );
                            log.info( "workorderEventsDistribution : sending to target={}", currentTarget );

                            this.streamBridge.send( target,
                                    MessageBuilder
                                            .withPayload( event )
                                            .setHeader( "workorderId", event.workorderId() )
                                            .build()
                            );

                        } else {
                            log.info( "workorderEventsDistribution : sending event" );

                            String target = String.format( "workorder-events-distribution-%s-out-0", event.node() );
                            log.info( "workorderEventsDistribution : sending to target={}", target );

                            this.streamBridge.send( target,
                                    MessageBuilder
                                            .withPayload( event )
                                            .setHeader( "workorderId", event.workorderId() )
                                            .build()
                            );

                        }

                    });

        }

    }

}
