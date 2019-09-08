package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.workorder.adapter.out.integration.serdes.ArrayListSerde;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EnableBinding( WorkorderBindingConfig.WorkorderEventsProcessor.class )
class WorkorderBindingConfig {

    private final Serde<WorkorderDomainEvent> domainEventSerde;
    private final SimpMessageSendingOperations messagingTemplate;

    WorkorderBindingConfig( final ObjectMapper objectMapper, final SimpMessageSendingOperations messagingTemplate ) {

        this.domainEventSerde = new JsonSerde<>( WorkorderDomainEvent.class, objectMapper );
        this.messagingTemplate = messagingTemplate;

    }

    @StreamListener( WorkorderEventsProcessor.INPUT_STREAM )
    void process( KStream<?, WorkorderDomainEvent> events ) {

        events
                .map( (key, value) -> new KeyValue<>( value.workorderId().toString(), value ) )
                .groupByKey( Serialized.with( Serdes.String(), this.domainEventSerde ) )
                .aggregate(
                        ArrayList::new,
                        (key, value, list) -> {

                            messagingTemplate.convertAndSend( "/topic/events", value );

                            list.add( value );
                            return list;
                        },
                        Materialized.<String, List<WorkorderDomainEvent>, KeyValueStore<Bytes, byte[]>>as( WorkorderEventsProcessor.WORKORDER_EVENTS_BY_ID )
                            .withKeySerde( Serdes.String() )
                                .withValueSerde( new ArrayListSerde( this.domainEventSerde ) )
                );

    }

    interface WorkorderEventsProcessor {

        String OUTPUT = "workorder-events-output";
        String INPUT_STREAM = "workorder-events-input";
        String WORKORDER_EVENTS_BY_ID = "workorder-events-by-id";

        @Output( OUTPUT )
        MessageChannel output();

        @Input( INPUT_STREAM )
        KStream<?, ?> inputStream();

    }

}
