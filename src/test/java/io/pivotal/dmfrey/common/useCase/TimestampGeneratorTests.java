package io.pivotal.dmfrey.common.useCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimestampGeneratorTests {

    private TimestampGenerator subject;

    @BeforeEach
    public void setup() {

        this.subject = new TimestampGenerator();

    }

    @Test
    public void testGenerate() {

        assertThat( this.subject.generate() ).isNotNull();

    }

}
