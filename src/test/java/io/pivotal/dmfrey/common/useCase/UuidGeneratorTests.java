package io.pivotal.dmfrey.common.useCase;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UuidGeneratorTests {

    private UuidGenerator subject;

    @Before
    public void setup() {

        this.subject = new UuidGenerator();

    }

    @Test
    public void testGenerate() {

        assertThat( this.subject.generate() ).isNotNull();

    }

}
