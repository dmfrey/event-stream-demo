package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.UUID;

public interface CreateWorkorderUseCase {

    UUID execute( CreateWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class CreateWorkorderCommand extends SelfValidating<CreateWorkorderCommand> {

        @NotEmpty
        @Size( max = 256 )
        private final String title;

        private final String targetNode;

        public CreateWorkorderCommand(
                final String title,
                final String targetNode
        ) {

            this.title = title;
            this.targetNode = targetNode;

            validateSelf();

        }

    }

}
