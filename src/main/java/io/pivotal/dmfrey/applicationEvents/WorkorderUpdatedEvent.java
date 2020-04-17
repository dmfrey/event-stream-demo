package io.pivotal.dmfrey.applicationEvents;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Value
@EqualsAndHashCode( callSuper = false )
public class WorkorderUpdatedEvent extends ApplicationEvent {

    String type;
    UUID workorderId;
    Map<String, Object> view;

    public WorkorderUpdatedEvent( final Object source, final String type, final UUID workorderId, final Map<String, Object> view ) {
        super( source );

        this.type = type;
        this.workorderId = workorderId;
        this.view = view;

    }

}
