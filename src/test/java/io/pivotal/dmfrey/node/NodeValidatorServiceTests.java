package io.pivotal.dmfrey.node;

import io.pivotal.dmfrey.node.application.in.NodeValidatorQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeValidatorServiceTests {

    private NodeValidatorService subject;

    private String fakeNode = "fakeNode";

    @BeforeEach
    public void setup() {

        this.subject = new NodeValidatorService( fakeNode );

    }

    @Test
    public void testExecute() {

        String actual = this.subject.execute( new NodeValidatorQuery.ValidateNodeCommand() );

        String expected = fakeNode;

        assertThat( actual ).isEqualTo( expected );

    }

    @Test
    public void testExecute_verifyNull_whenNodeIsCloud() {

        this.subject = new NodeValidatorService( "cloud" );
        String actual = this.subject.execute( new NodeValidatorQuery.ValidateNodeCommand() );

        String expected = "cloud";

        assertThat( actual ).isEqualTo( expected );

    }

}
