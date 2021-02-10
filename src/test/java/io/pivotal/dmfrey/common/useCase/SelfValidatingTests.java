package io.pivotal.dmfrey.common.useCase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

public class SelfValidatingTests {

    @Test
    public void testValidateSelf_verifyConstraintViolationException_whenNameIsNull() {

        Assertions.assertThrows( ConstraintViolationException.class, () -> {

            new TestClass( null );

        });

    }

    @Test
    public void testValidateSelf_verifyConstraintViolationException_whenNameIsEmpty() {

        Assertions.assertThrows( ConstraintViolationException.class, () -> {

            new TestClass( "" );

        });

    }

    final class TestClass extends SelfValidating<TestClass> {

        @NotEmpty
        private final String name;

        TestClass( final String name ) {

            this.name = name;

            validateSelf();
        }

    }

}
