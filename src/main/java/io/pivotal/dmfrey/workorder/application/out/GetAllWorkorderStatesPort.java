package io.pivotal.dmfrey.workorder.application.out;

import java.util.List;
import java.util.Map;

public interface GetAllWorkorderStatesPort {

    List<Map<String, Object>> getAllWorkorderStates();

}
