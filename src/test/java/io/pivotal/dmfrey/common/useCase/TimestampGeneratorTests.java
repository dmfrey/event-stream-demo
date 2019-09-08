package io.pivotal.dmfrey.common.useCase;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimestampGeneratorTests {

    private TimestampGenerator subject;

    @Before
    public void setup() {

        this.subject = new TimestampGenerator();

    }

    @Test
    public void testGenerate() {

        assertThat( this.subject.generate() ).isNotNull();

    }

}
