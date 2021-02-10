package io.pivotal.dmfrey.workorder.domain;

import io.pivotal.dmfrey.workorder.domain.WorkorderExceptions.*;
import io.pivotal.dmfrey.workorder.domain.events.*;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkorderTests {

    private UUID fakeWorkorderId = UUID.randomUUID();
    private String fakeTitle = "fakeTitle";

    private String fakeUser = "fakeUser";
    private String fakeNode = "fakeNode";

    @Test
    public void testWorkorder() {

        ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

        Workorder subject = new Workorder( fakeWorkorderId );

        assertThat( subject.id() ).isEqualTo( fakeWorkorderId );
        assertThat( subject.state() ).isEqualTo( "INITIALIZED" );

        subject.updateName( fakeTitle, fakeUser, fakeNode, fakeOccurredOn );

        NameUpdated expectedNameUpdatedEvent = new NameUpdated( fakeWorkorderId, fakeTitle, fakeUser, fakeNode, fakeOccurredOn );
        assertThat( subject.title() ).isEqualTo( fakeTitle );
        assertThat( subject.origination() ).isEqualTo( fakeNode );
        assertThat( subject.assigned() ).isEqualTo( fakeNode );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser );
        assertThat( subject.changes() )
                .hasSize( 1 )
                .containsAnyOf( expectedNameUpdatedEvent );

        subject.openWorkorder( fakeUser + "1", fakeNode, fakeOccurredOn.plusHours( 1 ) );

        WorkorderOpened expectedWorkorderOpened = new WorkorderOpened( fakeWorkorderId, fakeUser + "1", fakeNode, fakeOccurredOn.plusHours( 1 ) );
        assertThat( subject.state() ).isEqualTo( "OPEN" );
        assertThat( subject.isOpen() ).isTrue();
        assertThat( subject.assigned() ).isEqualTo( fakeNode );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser + "1" );
        assertThat( subject.changes() )
                .hasSize( 2 )
                .containsAnyOf( expectedNameUpdatedEvent, expectedWorkorderOpened );

        subject.startWorkorder( fakeUser + "2", fakeNode, fakeOccurredOn.plusHours( 2 ) );

        WorkorderStarted expectedWorkorderStarted = new WorkorderStarted( fakeWorkorderId, fakeUser + "2", fakeNode, fakeOccurredOn.plusHours( 2 ) );
        assertThat( subject.state() ).isEqualTo( "IN_PROCESS" );
        assertThat( subject.isInProcess() ).isTrue();
        assertThat( subject.startTime() ).isEqualTo( fakeOccurredOn.plusHours( 2 ) );
        assertThat( subject.assigned() ).isEqualTo( fakeNode );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser + "2" );
        assertThat( subject.changes() )
                .hasSize( 3 )
                .containsAnyOf( expectedNameUpdatedEvent, expectedWorkorderOpened, expectedWorkorderStarted );

        subject.stopWorkorder( fakeUser + "3", fakeNode, fakeOccurredOn.plusHours( 3 ) );

        WorkorderStopped expectedWorkorderStopped = new WorkorderStopped( fakeWorkorderId, fakeUser + "3", fakeNode, fakeOccurredOn.plusHours( 3 ) );
        assertThat( subject.state() ).isEqualTo( "IN_REVIEW" );
        assertThat( subject.isInReview() ).isTrue();
        assertThat( subject.endTime() ).isEqualTo( fakeOccurredOn.plusHours( 3 ) );
        assertThat( subject.assigned() ).isEqualTo( fakeNode );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser + "3" );
        assertThat( subject.changes() )
                .hasSize( 4 )
                .containsAnyOf( expectedNameUpdatedEvent, expectedWorkorderOpened, expectedWorkorderStarted, expectedWorkorderStopped );

        subject.completeWorkorder( fakeUser + "4", fakeNode, fakeOccurredOn.plusHours( 4 ) );

        WorkorderCompleted expectedWorkorderCompleted = new WorkorderCompleted( fakeWorkorderId, fakeUser + "4", fakeNode, fakeOccurredOn.plusHours( 4 ) );
        assertThat( subject.state() ).isEqualTo( "COMPLETE" );
        assertThat( subject.isComplete() ).isTrue();
        assertThat( subject.completedTime() ).isEqualTo( fakeOccurredOn.plusHours( 4 ) );
        assertThat( subject.assigned() ).isEqualTo( fakeNode );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser + "4" );
        assertThat( subject.changes() )
                .hasSize( 5 )
                .containsAnyOf( expectedNameUpdatedEvent, expectedWorkorderOpened, expectedWorkorderStarted, expectedWorkorderStopped, expectedWorkorderCompleted );

        subject.assignNode( fakeNode + "-test",fakeUser + "5", fakeNode, fakeOccurredOn.plusHours( 5 ) );

        NodeAssigned expectedNodeAssigned = new NodeAssigned( fakeWorkorderId, fakeNode, fakeNode + "-test", fakeUser + "5", fakeNode, fakeOccurredOn.plusHours( 5 ) );
        assertThat( subject.origination() ).isEqualTo( fakeNode );
        assertThat( subject.assigned() ).isEqualTo( fakeNode + "-test" );
        assertThat( subject.lastModifiedUser() ).isEqualTo( fakeUser + "5" );
        assertThat( subject.changes() )
                .hasSize( 6 )
                .containsAnyOf( expectedNameUpdatedEvent, expectedWorkorderOpened, expectedWorkorderStarted, expectedWorkorderStopped, expectedWorkorderCompleted, expectedNodeAssigned );

        subject.flushChanges();
        assertThat( subject.changes() ).isEmpty();

        Workorder replay = Workorder.createFrom( fakeWorkorderId, asList( expectedNameUpdatedEvent, expectedWorkorderStarted, expectedWorkorderStopped, expectedWorkorderCompleted, expectedNodeAssigned ) );
        assertThat( subject ).isEqualTo( replay );
    }

    @Test
    public void testOpenWorkorder_verifyWorkorderAlreadyOpenedException_whenAlreadyInProcess() {

        assertThrows( WorkorderAlreadyOpenedException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.openWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.openWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testOpenWorkorder_verifyWorkorderAlreadyInProcessException_whenAlreadyInProcess() {

        assertThrows( WorkorderAlreadyInProcessException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.startWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.openWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testOpenWorkorder_verifyWorkorderAlreadyReviewedException_whenAlreadyInReview() {

        assertThrows( WorkorderAlreadyReviewedException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.stopWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.openWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testOpenWorkorder_verifyWorkorderAlreadyCompleteException_whenAlreadyComplete() {

        assertThrows( WorkorderAlreadyCompleteException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.completeWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.openWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testStartWorkorder_verifyWorkorderAlreadyReviewedException_whenAlreadyInReview() {

        assertThrows( WorkorderAlreadyReviewedException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.stopWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.startWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testStartWorkorder_verifyWorkorderAlreadyCompleteException_whenAlreadyComplete() {

        assertThrows( WorkorderAlreadyCompleteException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.completeWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.startWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testStopWorkorder_verifyWorkorderAlreadyCompleteException_whenAlreadyComplete() {

        assertThrows( WorkorderAlreadyCompleteException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.completeWorkorder( fakeUser, fakeNode, fakeOccurredOn );

            subject.stopWorkorder( fakeUser, fakeNode, fakeOccurredOn );

        });

    }

    @Test
    public void testWorkorder_verifyMissingUserException_whenUserIsNull() {

        assertThrows( MissingUserException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.updateName( fakeTitle, null, null, fakeOccurredOn );

        });

    }

    @Test
    public void testWorkorder_verifyMissingUserException_whenUserIsEmpty() {

        assertThrows( MissingUserException.class, () -> {

            ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

            Workorder subject = new Workorder( fakeWorkorderId );
            subject.updateName( fakeTitle, "", fakeNode, fakeOccurredOn );

        });

    }

}
