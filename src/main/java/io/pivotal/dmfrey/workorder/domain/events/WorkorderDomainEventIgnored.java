package io.pivotal.dmfrey.workorder.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

import static lombok.AccessLevel.NONE;

@Value
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "workorderId", "user", "occurredOn" })
public class WorkorderDomainEventIgnored implements WorkorderDomainEvent {

    @Getter( NONE )
    private final UUID workorderId;

    @Getter( NONE )
    private final String user;

    @Getter( NONE )
    private final ZonedDateTime occurredOn;

    @JsonCreator
    public WorkorderDomainEventIgnored(
            @JsonProperty( "workorderId" ) final UUID workorderId,
            @JsonProperty( "user" ) final String user,
            @JsonProperty( "occurredOn" ) final ZonedDateTime occurredOn
    ) {

        this.workorderId = workorderId;
        this.user = user;
        this.occurredOn = occurredOn;

    }

    @Override
    @JsonProperty( "workorderId" )
    public UUID workorderId() {

        return null;
    }

    @Override
    @JsonProperty( "user" )
    public String user() {

        return null;
    }

    @Override
    @JsonProperty( "node" )
    public String node() {

        return null;
    }

    @Override
    @JsonProperty( "occurredOn" )
    public ZonedDateTime occurredOn() {

        return this.occurredOn;
    }

    @Override
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
