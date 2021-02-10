package io.pivotal.dmfrey.workorder.adapter.out.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.Application;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import io.pivotal.dmfrey.workorder.domain.events.WorkorderOpened;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NodeDistributionBindingConfigTests {

    final UUID fakeWorkorderId = UUID.randomUUID();
    final String fakeUser = "fakeUser";
    final String fakeNode = "fakeNode";
    final ZonedDateTime fakeOccurredOn = ZonedDateTime.now();

    @Test
    void testReceiveMessage_verifyOriginationNodeExists() throws JsonProcessingException {

        try( ConfigurableApplicationContext context =
                     new SpringApplicationBuilder( TestChannelBinderConfiguration.getCompleteConfiguration( Application.class ) )
                             .web( WebApplicationType.NONE )
                             .run(
                                     "--logging.level.org.springframework.integration=DEBUG",
                                     "--spring.profiles.active=node"
                             )
        ) {

            InputDestination inputDestination = context.getBean( InputDestination.class );
            OutputDestination outputDestination = context.getBean( OutputDestination.class );
            ObjectMapper mapper = context.getBean( ObjectMapper.class );

            Message<?> fakeMessage =
                    MessageBuilder
                            .withPayload( new WorkorderOpened( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn ) )
                            .setHeader( "originationNode", fakeNode )
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
                    .containsKey( "originationNode" );

        }

    }

    @Test
    void testNoMessage_verifyOriginationNodeDoesNotExists() throws JsonProcessingException {

        try( ConfigurableApplicationContext context =
                     new SpringApplicationBuilder( TestChannelBinderConfiguration.getCompleteConfiguration( Application.class ) )
                             .web( WebApplicationType.NONE )
                             .run(
                                     "--logging.level.org.springframework.integration=DEBUG",
                                     "--spring.profiles.active=node"
                             )
        ) {

            InputDestination inputDestination = context.getBean( InputDestination.class );
            StreamBridge streamBridge = context.getBean( StreamBridge.class );
            StreamBridge spy = spy( streamBridge );

            Message<?> fakeMessage =
                    MessageBuilder
                            .withPayload( new WorkorderOpened( fakeWorkorderId, fakeUser, fakeNode, fakeOccurredOn ) )
                            .build();
            inputDestination.send( fakeMessage );

            verify( spy, never() ).send( "workorder-events-distribution-cloud-out-0", fakeMessage );

        }

    }

}
