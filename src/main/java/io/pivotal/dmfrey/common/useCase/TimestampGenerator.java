package io.pivotal.dmfrey.common.useCase;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TimestampGenerator {

    public ZonedDateTime generate() {

        return ZonedDateTime.now( ZoneId.of( "UTC" ) );
    }

}
