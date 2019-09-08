package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface CompleteWorkorderUseCase {

    void execute( CompleteWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class CompleteWorkorderCommand extends SelfValidating<CompleteWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        public CompleteWorkorderCommand(
                final UUID workorderId
        ) {

            this.workorderId = workorderId;

            validateSelf();

        }

    }

}
