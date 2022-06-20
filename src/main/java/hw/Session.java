package hw;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Data
@ToString
public class Session {
    private final long duration;
    private LocalDateTime lastUpdate;
    private String value;

    public Session(String value) {
        this(TimeUnit.SECONDS.toSeconds(1800), value);
    }

    public Session(Long duration, String value) {
        this.duration = duration;
        lastUpdate = LocalDateTime.now();
        this.value = value;
    }
}
