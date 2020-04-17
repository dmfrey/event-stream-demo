package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.workorder.application.in.*;
import io.pivotal.dmfrey.workorder.application.in.CompleteWorkorderUseCase.CompleteWorkorderCommand;
import io.pivotal.dmfrey.workorder.application.in.GetAllWorkorderStatesQuery.GetAllWorkorderStatesCommand;
import io.pivotal.dmfrey.workorder.application.in.GetWorkorderQuery.GetWorkorderCommand;
import io.pivotal.dmfrey.workorder.application.in.OpenWorkorderUseCase.OpenWorkorderCommand;
import io.pivotal.dmfrey.workorder.application.in.StartWorkorderUseCase.StartWorkorderCommand;
import io.pivotal.dmfrey.workorder.application.in.StopWorkorderUseCase.StopWorkorderCommand;
import io.pivotal.dmfrey.workorder.application.in.TransferWorkorderUseCase.TransferWorkorderCommand;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final GetAllWorkorderStatesQuery getAllWorkorderStatesQuery;
    private final GetWorkorderQuery getWorkorderQuery;
    private final OpenWorkorderUseCase openWorkorderUseCase;
    private final StartWorkorderUseCase startWorkorderUseCase;
    private final StopWorkorderUseCase stopWorkorderUseCase;
    private final CompleteWorkorderUseCase completeWorkorderUseCase;
    private final TransferWorkorderUseCase transferWorkorderUseCase;
    private final ObjectMapper mapper;

    @Override
    protected void handleTextMessage( WebSocketSession session, TextMessage message ) throws Exception {
        log.info( "handleTextMessage : enter" );

        WebSocketRequest request = this.mapper.readValue( message.asBytes(), WebSocketRequest.class );
        log.info( "handleTextMessage : request = {}", request );

        if( request instanceof WorkordersRequest ) {

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorders",
                            "workorders", this.getAllWorkorderStatesQuery.execute( new GetAllWorkorderStatesCommand() )
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent workorders back to websocket" );

        }

        if( request instanceof WorkorderRequest ) {

            WorkorderRequest openWorkorderRequest = (WorkorderRequest) request;

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", openWorkorderRequest.type(),
                            "workorderId", openWorkorderRequest.getWorkorderId(),
                            "workorder", this.getWorkorderQuery.execute( new GetWorkorderCommand( openWorkorderRequest.getWorkorderId() ) )
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent workorder back to websocket" );

        }

        if( request instanceof OpenWorkorderRequest ) {

            OpenWorkorderRequest openWorkorderRequest = (OpenWorkorderRequest) request;
            Map<String, Object> workorder = this.openWorkorderUseCase.execute( new OpenWorkorderCommand( openWorkorderRequest.getWorkorderId() ) );

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", openWorkorderRequest.type(),
                            "workorderId", openWorkorderRequest.getWorkorderId(),
                            "workorder", workorder
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent updated workorder back to websocket" );

        }

        if( request instanceof StartWorkorderRequest ) {

            StartWorkorderRequest startWorkorderRequest = (StartWorkorderRequest) request;
            Map<String, Object> workorder = this.startWorkorderUseCase.execute( new StartWorkorderCommand( startWorkorderRequest.getWorkorderId() ) );

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", startWorkorderRequest.type(),
                            "workorderId", startWorkorderRequest.getWorkorderId(),
                            "workorder", workorder
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent updated workorder back to websocket" );

        }

        if( request instanceof StopWorkorderRequest ) {

            StopWorkorderRequest stopWorkorderRequest = (StopWorkorderRequest) request;
            Map<String, Object> workorder = this.stopWorkorderUseCase.execute( new StopWorkorderCommand( stopWorkorderRequest.getWorkorderId() ) );

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", stopWorkorderRequest.type(),
                            "workorderId", stopWorkorderRequest.getWorkorderId(),
                            "workorder", workorder
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent updated workorder back to websocket" );

        }

        if( request instanceof CompleteWorkorderRequest ) {

            CompleteWorkorderRequest completeWorkorderRequest = (CompleteWorkorderRequest) request;
            Map<String, Object> workorder = this.completeWorkorderUseCase.execute( new CompleteWorkorderCommand( completeWorkorderRequest.getWorkorderId() ) );

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", completeWorkorderRequest.type(),
                            "workorderId", completeWorkorderRequest.getWorkorderId(),
                            "workorder", workorder
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent updated workorder back to websocket" );

        }

        if( request instanceof TransferWorkorderRequest ) {

            TransferWorkorderRequest transferWorkorderRequest = (TransferWorkorderRequest) request;
            Map<String, Object> workorder = this.transferWorkorderUseCase.execute( new TransferWorkorderCommand( transferWorkorderRequest.getWorkorderId(), transferWorkorderRequest.getTargetNode() ) );

            Map<String, Object> websocketResponse =
                    Map.of(
                            "type", "Workorder",
                            "action", transferWorkorderRequest.type(),
                            "workorderId", transferWorkorderRequest.getWorkorderId(),
                            "targetNode", transferWorkorderRequest.getTargetNode(),
                            "workorder", workorder
                    );

            String responseJson = this.mapper.writeValueAsString( websocketResponse );
            log.info( "handleTextMessage : responseJson = {}", responseJson );

            WebSocketMessage<String> msg = new TextMessage( responseJson );
            session.sendMessage( msg );
            log.info( "handleTextMessage : sent updated workorder back to websocket" );

        }

        log.info( "handleTextMessage : exit" );
    }

    @Override
    protected void handleBinaryMessage( WebSocketSession session, BinaryMessage message ) throws Exception {
        log.info( "handleBinaryMessage : enter" );

        log.info( "handleBinaryMessage : exit" );
    }

}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = DefaultWebSocketRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type( value = WorkordersRequest.class, name = "WorkordersRequest" ),
        @JsonSubTypes.Type( value = WorkorderRequest.class, name = "WorkorderRequest" ),
        @JsonSubTypes.Type( value = OpenWorkorderRequest.class, name = "OpenWorkorderRequest" ),
        @JsonSubTypes.Type( value = StartWorkorderRequest.class, name = "StartWorkorderRequest" ),
        @JsonSubTypes.Type( value = StopWorkorderRequest.class, name = "StopWorkorderRequest" ),
        @JsonSubTypes.Type( value = CompleteWorkorderRequest.class, name = "CompleteWorkorderRequest" ),
        @JsonSubTypes.Type( value = TransferWorkorderRequest.class, name = "TransferWorkorderRequest" )
})
interface WebSocketRequest {

    @JsonIgnore
    String type();

}

class DefaultWebSocketRequest implements WebSocketRequest {

    @Override
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type" })
class WorkordersRequest implements WebSocketRequest {

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId" })
@JsonIgnoreProperties( ignoreUnknown = true )
class WorkorderRequest implements WebSocketRequest {

    UUID workorderId;

    @JsonCreator
    public WorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId
    ) {

        this.workorderId = workorderId;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId" })
@JsonIgnoreProperties( ignoreUnknown = true )
class OpenWorkorderRequest implements WebSocketRequest {

    UUID workorderId;

    @JsonCreator
    public OpenWorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId
    ) {

        this.workorderId = workorderId;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId" })
@JsonIgnoreProperties( ignoreUnknown = true )
class StartWorkorderRequest implements WebSocketRequest {

    UUID workorderId;

    @JsonCreator
    public StartWorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId
    ) {

        this.workorderId = workorderId;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId" })
@JsonIgnoreProperties( ignoreUnknown = true )
class StopWorkorderRequest implements WebSocketRequest {

    UUID workorderId;

    @JsonCreator
    public StopWorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId
    ) {

        this.workorderId = workorderId;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId" })
@JsonIgnoreProperties( ignoreUnknown = true )
class CompleteWorkorderRequest implements WebSocketRequest {

    UUID workorderId;

    @JsonCreator
    public CompleteWorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId
    ) {

        this.workorderId = workorderId;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}

@Value
@JsonPropertyOrder({ "type", "workorderId", "targetNode" })
@JsonIgnoreProperties( ignoreUnknown = true )
class TransferWorkorderRequest implements WebSocketRequest {

    UUID workorderId;
    String targetNode;

    @JsonCreator
    public TransferWorkorderRequest(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "targetNode" ) final String targetNode
    ) {

        this.workorderId = workorderId;
        this.targetNode = targetNode;

    }

    @Override
    @JsonProperty
    public String type() {

        return getClass().getSimpleName();
    }

}