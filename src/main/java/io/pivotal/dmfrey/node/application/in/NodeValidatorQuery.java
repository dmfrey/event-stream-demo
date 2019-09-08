package io.pivotal.dmfrey.node.application.in;

public interface NodeValidatorQuery {

    String execute( ValidateNodeCommand command );

    final class ValidateNodeCommand {}

}
