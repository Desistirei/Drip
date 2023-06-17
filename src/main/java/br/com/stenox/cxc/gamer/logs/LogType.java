package br.com.stenox.cxc.gamer.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LogType {

    VANISH("%player1% entrou/saiu no modo vanish"),
    TELEPORT("%player1% se teleportou para %player2%"),
    OPEN_INVENTORY("%player1% abriu o inventário de %player2%");

    private final String message;

}
