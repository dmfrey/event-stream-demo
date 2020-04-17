package io.pivotal.dmfrey.workorder.application.in;

import io.pivotal.dmfrey.common.useCase.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

public interface TransferWorkorderUseCase {

    Map<String, Object> execute( TransferWorkorderCommand command );

    @Getter
    @EqualsAndHashCode( callSuper = false )
    final class TransferWorkorderCommand extends SelfValidating<TransferWorkorderCommand> {

        @NotNull
        private final UUID workorderId;

        @NotEmpty
        @Size( max = 256 )
        private final String targetNode;

        public TransferWorkorderCommand(
                final UUID workorderId,
                final String targetNode
        ) {

            this.workorderId = workorderId;
            this.targetNode = targetNode;

            validateSelf();

        }

    }

}
