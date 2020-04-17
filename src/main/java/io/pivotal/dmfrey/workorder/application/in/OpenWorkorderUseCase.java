package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public interface OpenWorkorderUseCase {

    Map<String, Object> execute( OpenWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class OpenWorkorderCommand extends SelfValidating<OpenWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        public OpenWorkorderCommand(
                final UUID workorderId
        ) {

            this.workorderId = workorderId;

            validateSelf();

        }

    }

}
