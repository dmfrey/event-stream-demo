package io.pivotal.dmfrey.workorder.adapter.out.integration;

import io.pivotal.dmfrey.workorder.domain.events.NodeAssigned;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.filter.ExpressionEvaluatingSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;

@Slf4j
@Configuration
class DistributionBindingConfig {

    @Profile( "!cloud" )
    @Configuration
    static class Node {

        private static final String OUTPUT_CLOUD = "workorder-events-distribution-cloud-out-0";

        @Bean
        public Consumer<Message<WorkorderDomainEvent>> workorderEventsDistribution( final StreamBridge streamBridge, final ExpressionEvaluatingSelector headerExpressionEvaluatingSelector ) {

            return message -> {
                log.debug( "workorderEventsDistribution : message={}", message );

                if( headerExpressionEvaluatingSelector.accept( message ) ) {

                    streamBridge.send( OUTPUT_CLOUD, message );
                }

            };
        }

        @Bean
        public ExpressionEvaluatingSelector headerExpressionEvaluatingSelector() {

            return new ExpressionEvaluatingSelector( "headers.containsKey('originationNode')" );
        }

    }

    @Configuration
    @Profile( "cloud" )
    static class Cloud {

        private static final String OUTPUT_TARGET_TEMPLATE = "workorder-events-distribution-%s-out-0";

        @Bean
        public Consumer<Message<WorkorderDomainEvent>> workorderEventsDistribution( final StreamBridge streamBridge, final WorkorderPersistenceAdapter workorderPersistenceAdapter ) {

            return message -> {
                log.debug( "workorderEventsDistribution : message={}", message );

                WorkorderDomainEvent event = message.getPayload();
                if( event instanceof NodeAssigned ) {
                    log.debug( "workorderEventsDistribution : transfer initiated, sending all events" );

                    final String target = String.format( OUTPUT_TARGET_TEMPLATE, ((NodeAssigned) event ).targetNode() );
                    log.debug( "workorderEventsDistribution : sending all to target={}", target );

                    workorderPersistenceAdapter.getWorkorderEvents( event.workorderId() )
                            .forEach( e -> {
                                log.debug( "workorderEventsDistribution : event={}", e );

                                streamBridge.send( target,
                                        MessageBuilder
                                                .withPayload( e )
                                                .setHeader( "workorderId", e.workorderId() )
                                                .build()
                                );

                            });

                    streamBridge.send( target,
                            MessageBuilder
                                    .withPayload( event )
                                    .setHeader("workorderId", event.workorderId() )
                                    .build()
                    );

                    String currentTarget = String.format( OUTPUT_TARGET_TEMPLATE, ((NodeAssigned) event).currentNode() );
                    log.debug( "workorderEventsDistribution : sending to target={}", currentTarget );

                    streamBridge.send( currentTarget,
                            MessageBuilder
                                    .withPayload( event )
                                    .setHeader("workorderId", event.workorderId() )
                                    .build()
                    );

                } else {
                    log.debug( "workorderEventsDistribution : sending event" );

                    String target = String.format( OUTPUT_TARGET_TEMPLATE, event.node() );
                    log.debug( "workorderEventsDistribution : sending to target={}", target );

                    streamBridge.send( target,
                            MessageBuilder
                                    .withPayload( event )
                                    .setHeader("workorderId", event.workorderId() )
                                    .build()
                    );
                }

            };

        }

    }

}
