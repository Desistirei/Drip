package br.com.stenox.cxc.event.game;

import br.com.stenox.cxc.event.CustomEvent;
import br.com.stenox.cxc.game.stage.GameStage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GameChangeStageEvent extends CustomEvent {

    private final GameStage oldStage;
    private final GameStage newStage;
}
