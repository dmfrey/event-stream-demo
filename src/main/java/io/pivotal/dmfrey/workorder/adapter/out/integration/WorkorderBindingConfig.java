package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.workorder.adapter.out.integration.serdes.ArrayListSerde;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
class WorkorderBindingConfig {

    private final Serde<WorkorderDomainEvent> domainEventSerde;

    WorkorderBindingConfig( final ObjectMapper mapper ) {

        this.domainEventSerde = new JsonSerde<>( WorkorderDomainEvent.class, mapper );

    }

    @Bean
    public Consumer<KStream<Object, WorkorderDomainEvent>> workorderEvents() {

        return events -> events
                .map( (key, value) -> new KeyValue<>( value.workorderId(), value ) )
                .groupByKey( Grouped.with( Serdes.UUID(), this.domainEventSerde ) )
                .aggregate(
                        ArrayList<WorkorderDomainEvent>::new,
                        (workorderId, workorderDomainEvent, aggregate) -> {

                            aggregate.add( workorderDomainEvent );
                            return aggregate;
                        },
                        Materialized.<UUID, List<WorkorderDomainEvent>, KeyValueStore<Bytes, byte[]>>as( "workorder-events-by-id" )
                                .withKeySerde( Serdes.UUID() )
                                .withValueSerde( new ArrayListSerde( this.domainEventSerde ) )
                );

    }

}
