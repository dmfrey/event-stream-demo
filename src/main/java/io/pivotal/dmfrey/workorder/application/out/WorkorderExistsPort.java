package io.pivotal.dmfrey.workorder.application.out;

import java.util.UUID;

public interface WorkorderExistsPort {

    boolean workorderExists( UUID workorderId );

}
