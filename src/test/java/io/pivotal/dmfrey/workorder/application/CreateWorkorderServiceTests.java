package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.TimestampGenerator;
import io.pivotal.dmfrey.common.useCase.UuidGenerator;
import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import io.pivotal.dmfrey.workorder.application.in.CreateWorkorderUseCase;
import io.pivotal.dmfrey.workorder.application.out.PersistWorkorderEventPort;
import io.pivotal.dmfrey.workorder.domain.events.NameUpdated;
import io.pivotal.dmfrey.workorder.domain.events.NodeAssigned;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CreateWorkorderServiceTests {

    private CreateWorkorderService subject;

    private PersistWorkorderEventPort mockPersistWorkorderEventsPort;
    private NodeValidatorQuery mockNodeValidatorQuery;
    private UuidGenerator mockUuidGenerator;
    private TimestampGenerator mockTimestampGenerator;

    private UUID fakeWorkorderId = UUID.randomUUID();
    private String fakeTitle = "fakeTitle";
    private String fakeUser = "testUser";
    private String fakeNode = "fakeNode";

    @BeforeEach
    public void setup() {

        this.mockPersistWorkorderEventsPort = mock( PersistWorkorderEventPort.class );
        this.mockNodeValidatorQuery = mock( NodeValidatorQuery.class );
        this.mockUuidGenerator = mock( UuidGenerator.class );
        this.mockTimestampGenerator = mock( TimestampGenerator.class );

        this.subject = new CreateWorkorderService( this.mockPersistWorkorderEventsPort, this.mockNodeValidatorQuery, this.mockUuidGenerator, this.mockTimestampGenerator );

        when( this.mockNodeValidatorQuery.execute( any(NodeValidatorQuery.ValidateNodeCommand.class ) ) ).thenReturn( fakeNode );

    }

    @Test
    public void testExecute() {

        ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );
        when( this.mockTimestampGenerator.generate() ).thenReturn( fakeOccurredOn );

        when( this.mockUuidGenerator.generate() ).thenReturn( fakeWorkorderId );

        CreateWorkorderUseCase.CreateWorkorderCommand fakeCommand = new CreateWorkorderUseCase.CreateWorkorderCommand( fakeTitle, null );
        UUID actual = this.subject.execute( fakeCommand );

        UUID expected = fakeWorkorderId;
        NameUpdated expectedNameUpdatedEvent = new NameUpdated( fakeWorkorderId, fakeTitle, fakeUser, fakeNode, fakeOccurredOn );

        assertThat( actual ).isEqualTo( expected );

        verify( this.mockPersistWorkorderEventsPort ).save( fakeWorkorderId, singletonList( expectedNameUpdatedEvent ) );
        verify( this.mockUuidGenerator ).generate();
        verify( this.mockTimestampGenerator ).generate();
        verifyNoMoreInteractions( this.mockPersistWorkorderEventsPort, this.mockUuidGenerator, this.mockTimestampGenerator );

    }

    @Test
    public void testExecute_verifyAircraftAssigned_whenTargetNodeIsProvided() {

        ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );
        when( this.mockTimestampGenerator.generate() ).thenReturn( fakeOccurredOn );

        when( this.mockUuidGenerator.generate() ).thenReturn( fakeWorkorderId );

        CreateWorkorderUseCase.CreateWorkorderCommand fakeCommand = new CreateWorkorderUseCase.CreateWorkorderCommand( fakeTitle, "fakeTargetNode" );
        UUID actual = this.subject.execute( fakeCommand );

        UUID expected = fakeWorkorderId;
        NameUpdated expectedNameUpdatedEvent = new NameUpdated( fakeWorkorderId, fakeTitle, fakeUser, fakeNode, fakeOccurredOn );
        NodeAssigned expectedNodeAssignedEvent = new NodeAssigned( fakeWorkorderId, fakeNode, "fakeTargetNode", fakeUser, fakeNode, fakeOccurredOn );

        assertThat( actual ).isEqualTo( expected );

        verify( this.mockPersistWorkorderEventsPort ).save( fakeWorkorderId, asList( expectedNameUpdatedEvent, expectedNodeAssignedEvent ) );
        verify( this.mockUuidGenerator ).generate();
        verify( this.mockTimestampGenerator, times( 2 ) ).generate();
        verifyNoMoreInteractions( this.mockPersistWorkorderEventsPort, this.mockUuidGenerator, this.mockTimestampGenerator );

    }

}
