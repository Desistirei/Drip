package br.com.stenox.cxc.event.custom;

import br.com.stenox.cxc.event.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimeSecondEvent extends CustomEvent {

    private TimeType type;

    public enum TimeType {

        MILLISECONDS, SECONDS;
    }
}
