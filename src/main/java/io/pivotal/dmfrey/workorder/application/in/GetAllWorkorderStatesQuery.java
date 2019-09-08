package io.pivotal.dmfrey.workorder.application.in;

import java.util.List;
import java.util.Map;

public interface GetAllWorkorderStatesQuery {

    List<Map<String, Object>> execute( GetAllWorkorderStatesCommand command);

    final class GetAllWorkorderStatesCommand { }

}
