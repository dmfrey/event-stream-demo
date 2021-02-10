package io.pivotal.dmfrey.common.useCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UuidGeneratorTests {

    private UuidGenerator subject;

    @BeforeEach
    public void setup() {

        this.subject = new UuidGenerator();

    }

    @Test
    public void testGenerate() {

        assertThat( this.subject.generate() ).isNotNull();

    }

}
