package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class WorkorderBindingConfig {

    private final Serde<WorkorderDomainEvent> domainEventSerde;

    WorkorderBindingConfig( final ObjectMapper mapper ) {

        this.domainEventSerde = new JsonSerde<>( WorkorderDomainEvent.class, mapper );

    }

//    @Bean
//    Consumer<KStream<Object, WorkorderDomainEvent>> workorderEventsTableTransformer() {
//
//        return events -> events
//                .map( (key, value) -> new KeyValue<>( value.workorderId().toString(), value ) )
//                .groupByKey( Grouped.with( Serdes.String(), this.domainEventSerde ) )
//                .aggregate(
//                        ArrayList::new,
//                        (key, value, list) -> {
//
//                            list.add( value );
//                            return list;
//                        },
//                        Materialized.<String, List<WorkorderDomainEvent>, KeyValueStore<Bytes, byte[]>>as( WorkorderEventsProcessor.WORKORDER_EVENTS_BY_ID )
//                            .withKeySerde( Serdes.String() )
//                                .withValueSerde( new ArrayListSerde( this.domainEventSerde ) )
//                );
//
//    }

    interface WorkorderEventsProcessor {

//        String OUTPUT = "workorder-events-output";
//        String INPUT_STREAM = "workorder-events-input";
        String WORKORDER_EVENTS_BY_ID = "workorder-events-by-id";

//        @Output( OUTPUT )
//        MessageChannel output();

//        @Input( INPUT_STREAM )
//        KStream<?, ?> inputStream();

    }

}
