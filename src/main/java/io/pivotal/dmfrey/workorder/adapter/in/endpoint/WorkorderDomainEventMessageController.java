//package io.pivotal.dmfrey.workorder.adapter.in.endpoint;
//
//import io.pivotal.dmfrey.workorder.application.in.GetAllWorkorderStatesQuery;
//import io.pivotal.dmfrey.workorder.application.in.GetAllWorkorderStatesQuery.GetAllWorkorderStatesCommand;
//import io.pivotal.dmfrey.workorder.domain.events.WorkorderDomainEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequiredArgsConstructor
//public class WorkorderDomainEventMessageController {
//
//    private final GetAllWorkorderStatesQuery useCase;
//
//    @MessageMapping( "/history" )
//    @SendTo( "/queue/history" )
//    public List<Map<String, Object>> history() {
//
//        return useCase.execute( new GetAllWorkorderStatesCommand() );
//    }
//
//    @MessageMapping( "/events" )
//    @SendTo( "/topic/events" )
//    public WorkorderDomainEvent event( @Payload WorkorderDomainEvent event ) {
//
//        return event;
//    }
//
//}
