package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public interface StopWorkorderUseCase {

    Map<String, Object> execute( StopWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class StopWorkorderCommand extends SelfValidating<StopWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        public StopWorkorderCommand(
                final UUID workorderId
        ) {

            this.workorderId = workorderId;

            validateSelf();

        }

    }

}
