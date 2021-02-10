package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.workorder.adapter.out.integration.serdes.ArrayListSerde;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
class WorkorderBindingConfig {

    private static final String WORKORDER_EVENTS_BY_ID = "workorder-events-by-id";

    private final Serde<WorkorderDomainEvent> domainEventSerde;
    private final SimpMessageSendingOperations messagingTemplate;

    WorkorderBindingConfig( final ObjectMapper objectMapper, final SimpMessageSendingOperations messagingTemplate ) {

        this.domainEventSerde = new JsonSerde<>( WorkorderDomainEvent.class, objectMapper );
        this.messagingTemplate = messagingTemplate;

    }

    @Bean
    public Consumer<KStream<?, WorkorderDomainEvent>> process() {

        return events -> events
                .map( (key, value) -> new KeyValue<>( value.workorderId().toString(), value ) )
                .groupByKey( Grouped.with( Serdes.String(), this.domainEventSerde ) )
                .aggregate(
                        ArrayList::new,
                        (key, value, list) -> {

                            messagingTemplate.convertAndSend( "/topic/events", value );

                            list.add( value );
                            return list;
                        },
                        Materialized.<String, List<WorkorderDomainEvent>, KeyValueStore<Bytes, byte[]>>as( WORKORDER_EVENTS_BY_ID )
                                .withKeySerde( Serdes.String() )
                                .withValueSerde( new ArrayListSerde( this.domainEventSerde ) )
                );
    }

}
