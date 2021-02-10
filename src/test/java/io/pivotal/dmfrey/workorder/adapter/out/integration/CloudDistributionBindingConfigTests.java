package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.Application;
import io.pivotal.dmfrey.workorder.domain.events.NodeAssigned;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderOpened;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderStarted;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CloudDistributionBindingConfigTests {

    final static UUID fakeWorkorderId = UUID.randomUUID();
    final static String fakeUser = "fakeUser";
    final static String fakeNode = "fakeNode";
    final static ZonedDateTime fakeOccurredOn = ZonedDateTime.now();

    @Test
    void testReceiveMessage_verifySendingToNodeDistribution() throws JsonProcessingException {

        try( ConfigurableApplicationContext context =
                     new SpringApplicationBuilder( TestChannelBinderConfiguration.getCompleteConfiguration( Application.class ) )
                             .web( WebApplicationType.NONE )
                             .run(
                                     "--logging.level.org.springframework.integration=DEBUG",
                                     "--spring.profiles.active=cloud",
                                     "--spring.cloud.stream.bindings.workorder-events-distribution-fakeNode-out-0.destination=${node.current}.distribution-fakeNode"
                             )
        ) {

            InputDestination inputDestination = context.getBean( InputDestination.class );
            OutputDestination outputDestination = context.getBean( OutputDestination.class );
            ObjectMapper mapper = context.getBean( ObjectMapper.class );

            Message<?> fakeMessage =
                    MessageBuilder
                            .withPayload( new WorkorderOpened( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn ) )
                            .setHeader( "workorderId", fakeWorkorderId )
                            .build();
            inputDestination.send( fakeMessage );

            Message<byte[]> actual = outputDestination.receive( 10000 );

            WorkorderDomainEvent actualEvent = mapper.readValue( new String( actual.getPayload() ), WorkorderDomainEvent.class );
            assertThat( actualEvent.eventType() ).isEqualTo( "WorkorderOpened" );
            assertThat( actualEvent.workorderId() ).isEqualTo( fakeWorkorderId );
            assertThat( actualEvent.user() ).isEqualTo( fakeUser );
            assertThat( actualEvent.node() ).isEqualTo( fakeNode );
            assertThat( actualEvent.occurredOn() ).isEqualTo( fakeOccurredOn );

            assertThat( actual.getHeaders() )
                    .containsEntry( "workorderId", fakeWorkorderId );

        }

    }

    @Test
    void testReceiveMessage_verifyNodeTransfer() throws JsonProcessingException {

        try( ConfigurableApplicationContext context =
                     new SpringApplicationBuilder( TestChannelBinderConfiguration.getCompleteConfiguration( TestApplication.class ) )
                             .web( WebApplicationType.NONE )
                             .run(
                                     "--logging.level.org.springframework.integration=DEBUG",
                                     "--spring.profiles.active=cloud",
                                     "--spring.cloud.stream.bindings.workorder-events-distribution-fakeNode-out-0.destination=${node.current}.distribution-fakeNode",
                                     "--spring.cloud.stream.bindings.workorder-events-distribution-oldNode-out-0.destination=${node.current}.distribution-oldNode"
                             )
        ) {

            InputDestination inputDestination = context.getBean( InputDestination.class );
            OutputDestination outputDestination = context.getBean( OutputDestination.class );

            ObjectMapper mapper = context.getBean( ObjectMapper.class );

            Message<?> fakeMessage =
                    MessageBuilder
                            .withPayload( new NodeAssigned( fakeWorkorderId, "oldNode", fakeNode, fakeUser, "oldNode", fakeOccurredOn ) )
                            .build();
            inputDestination.send( fakeMessage );

            Message<byte[]> actual1 = outputDestination.receive();

            WorkorderOpened actualEvent1 = mapper.readValue( new String( actual1.getPayload() ), WorkorderOpened.class );
            assertThat( actualEvent1.eventType() ).isEqualTo( "WorkorderOpened" );
            assertThat( actualEvent1.workorderId() ).isEqualTo( fakeWorkorderId );
            assertThat( actualEvent1.user() ).isEqualTo( fakeUser );
            assertThat( actualEvent1.node() ).isEqualTo( fakeNode );
            assertThat( actualEvent1.occurredOn() ).isEqualTo( fakeOccurredOn );

            assertThat( actual1.getHeaders() )
                    .containsEntry( "workorderId", fakeWorkorderId );

            Message<byte[]> actual2 = outputDestination.receive();

            WorkorderStarted actualEvent2 = mapper.readValue( new String( actual2.getPayload() ), WorkorderStarted.class );
            assertThat( actualEvent2.eventType() ).isEqualTo( "WorkorderStarted" );
            assertThat( actualEvent2.workorderId() ).isEqualTo( fakeWorkorderId );
            assertThat( actualEvent2.user() ).isEqualTo( fakeUser );
            assertThat( actualEvent2.node() ).isEqualTo( fakeNode );
            assertThat( actualEvent2.occurredOn() ).isEqualTo( fakeOccurredOn );

            assertThat( actual2.getHeaders() )
                    .containsEntry( "workorderId", fakeWorkorderId );

            Message<byte[]> actual3 = outputDestination.receive();

            NodeAssigned actualEvent3 = mapper.readValue( new String( actual3.getPayload() ), NodeAssigned.class );
            assertThat( actualEvent3.eventType() ).isEqualTo( "NodeAssigned" );
            assertThat( actualEvent3.workorderId() ).isEqualTo( fakeWorkorderId );
            assertThat( actualEvent3.currentNode() ).isEqualTo( "oldNode" );
            assertThat( actualEvent3.targetNode() ).isEqualTo( fakeNode );
            assertThat( actualEvent3.user() ).isEqualTo( fakeUser );
            assertThat( actualEvent3.node() ).isEqualTo( "oldNode" );
            assertThat( actualEvent3.occurredOn() ).isEqualTo( fakeOccurredOn );

            assertThat( actual3.getHeaders() )
                    .containsEntry( "workorderId", fakeWorkorderId );

        }

    }

    @SpringBootApplication
    @Import( Application.class )
    static class TestApplication {

        @Bean
        @Primary
        WorkorderEventsLookup mockWorkorderEventsLookup() {

            WorkorderEventsLookup mock = mock( WorkorderEventsLookup.class );
            when( mock.lookupByWorkorderId( fakeWorkorderId ) ).thenReturn(
                    List.of(
                            new WorkorderOpened( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn ),
                            new WorkorderStarted( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn )
                    )
            );

            return mock;
        }

        @Bean
        @Primary
        WorkorderPersistenceAdapter mockWorkorderPersistenceAdapter( final WorkorderEventsLookup mockWorkorderEventsLookup ) {

            return new WorkorderPersistenceAdapter( mockWorkorderEventsLookup, null, null );

        }

    }

}
