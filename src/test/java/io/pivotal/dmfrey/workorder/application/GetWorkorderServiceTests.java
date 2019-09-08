package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.workorder.application.in.GetWorkorderQuery;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.domain.events.NameUpdated;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GetWorkorderServiceTests {

    private GetWorkorderService subject;

    private GetWorkorderEventsPort mockGetWorkorderEventsPort;

    private UUID fakeWorkorderId = UUID.randomUUID();
    private String fakeTitle = "fakeTitle";
    private String fakeState = "INITIALIZED";
    private String fakeUser = "fakeUser";
    private String fakeNode = "fakeNode";

    @Before
    public void setup() {

        this.mockGetWorkorderEventsPort = mock( GetWorkorderEventsPort.class );

        this.subject = new GetWorkorderService( this.mockGetWorkorderEventsPort );

    }

    @Test
    public void testExecute() {

        ZonedDateTime fakeOccurredOn = ZonedDateTime.now( ZoneId.of( "UTC" ) );

        NameUpdated fakeNameUpdatedEvent = new NameUpdated( fakeWorkorderId, fakeTitle, fakeUser, fakeNode, fakeOccurredOn );
        when( this.mockGetWorkorderEventsPort.getWorkorderEvents( fakeWorkorderId ) ).thenReturn( singletonList( fakeNameUpdatedEvent ) );

        GetWorkorderQuery.GetWorkorderCommand fakeCommand = new GetWorkorderQuery.GetWorkorderCommand( fakeWorkorderId );
        Map<String, Object> actual = this.subject.execute( fakeCommand );

        Map<String, Object> expected = new HashMap<>();
        expected.put( "workorderId", fakeWorkorderId );
        expected.put( "title", fakeTitle );
        expected.put( "state", fakeState );
        expected.put( "assigned", fakeNode );
        expected.put( "origination", fakeNode );

        assertThat( actual ).isEqualTo( expected );

        verify( this.mockGetWorkorderEventsPort ).getWorkorderEvents( fakeWorkorderId );
        verifyNoMoreInteractions( this.mockGetWorkorderEventsPort );

    }

}
