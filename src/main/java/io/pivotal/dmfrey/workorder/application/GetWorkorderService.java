package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.workorder.application.in.GetWorkorderQuery;
import io.pivotal.dmfrey.workorder.application.out.GetWorkorderEventsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.pivotal.dmfrey.workorder.domain.Workorder;

import java.util.HashMap;
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

        Map<String, Object> view = new HashMap<>();
        view.put( "workorderId", foundWorker.id() );
        view.put( "title", foundWorker.title() );
        view.put( "state", foundWorker.state() );
        view.put( "assigned", foundWorker.assigned() );
        view.put( "origination", foundWorker.origination() );

        return view;
    }

}
