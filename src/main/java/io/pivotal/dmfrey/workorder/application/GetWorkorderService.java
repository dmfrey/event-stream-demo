package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.workorder.application.in.GetWorkorderQuery;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import io.pivotal.dmfrey.workorder.domain.Workorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@UseCase
@RequiredArgsConstructor
class GetWorkorderService implements GetWorkorderQuery {

    private final GetWorkorderEventsPort getWorkorderEventsPort;

    @Override
    public Map<String, Object> execute( final GetWorkorderCommand command ) {

        Workorder foundWorker = Workorder.createFrom( command.getWorkorderId(), getWorkorderEventsPort.getWorkorderEvents( command.getWorkorderId() ) );
        foundWorker.flushChanges();

        return foundWorker.getWorkorderView();
    }

}
