package br.com.stenox.cxc.event.player;

import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.state.GamerState;
import br.com.stenox.cxc.event.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerChangeStateEvent extends CustomEvent {

    private Gamer gamer;
    private GamerState newState;
}
