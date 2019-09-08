package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public interface GetWorkorderQuery {

    Map<String, Object> execute( GetWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class GetWorkorderCommand extends SelfValidating<GetWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        public GetWorkorderCommand(
                final UUID workorderId
        ) {

            this.workorderId = workorderId;

            validateSelf();

        }

    }

}
