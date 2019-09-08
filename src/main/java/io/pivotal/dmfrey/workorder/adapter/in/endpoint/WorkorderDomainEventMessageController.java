package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WorkorderDomainEventMessageController {

    @MessageMapping( "/events" )
    @SendTo( "/topic/events" )
    public WorkorderDomainEvent event( @Payload WorkorderDomainEvent event ) {

        return event;
    }

}
