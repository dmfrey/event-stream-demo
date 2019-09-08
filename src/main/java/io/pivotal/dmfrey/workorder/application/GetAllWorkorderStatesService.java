package io.pivotal.dmfrey.workorder.application;

import io.pivotal.dmfrey.common.useCase.UseCase;
import io.pivotal.dmfrey.workorder.application.in.GetAllWorkorderStatesQuery;
import io.pivotal.dmfrey.workorder.application.out.GetAllWorkorderStatesPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@UseCase
@RequiredArgsConstructor
class GetAllWorkorderStatesService implements GetAllWorkorderStatesQuery {

    private final GetAllWorkorderStatesPort getAllWorkorderStatesPort;

    @Override
    public List<Map<String, Object>> execute( GetAllWorkorderStatesCommand command ) {

        return getAllWorkorderStatesPort.getAllWorkorderStates();
    }

}
