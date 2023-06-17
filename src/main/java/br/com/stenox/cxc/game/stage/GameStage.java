package br.com.stenox.cxc.game.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameStage {

    STARTING(240, 240, false), INVINCIBILITY(601, 61, false), IN_GAME(599, 59, true), ENDING(0, 0, false);

    private int startTimeCxC;
    private int startTimeEvent;
    private boolean increment;
}
