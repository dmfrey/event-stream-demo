package io.pivotal.dmfrey.common.useCase;

import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

public class SelfValidatingTests {

    @Test( expected = ConstraintViolationException.class )
    public void testValidateSelf_verifyConstraintViolationException_whenNameIsNull() {

        TestClass testClass = new TestClass( null );


    }

    @Test( expected = ConstraintViolationException.class )
    public void testValidateSelf_verifyConstraintViolationException_whenNameIsEmpty() {

        TestClass testClass = new TestClass( "" );


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
