package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface StartWorkorderUseCase {

    void execute( StartWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class StartWorkorderCommand extends SelfValidating<StartWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        public StartWorkorderCommand(
                final UUID workorderId
        ) {

            this.workorderId = workorderId;

            validateSelf();

        }

    }

}
