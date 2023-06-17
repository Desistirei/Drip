package br.com.stenox.cxc.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public enum SkinLibrary {

    CELLBIT("Cellbit", "Cellbit", "ewogICJ0aW1lc3RhbXAiIDogMTU5NDk1OTEzNjcwNSwKICAicHJvZmlsZUlkIiA6ICIwYTQ3NDJjOWQ3YWY0NmM4OTllODNlNTdiYWE0MTNmYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDZWxsYml0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM4ZDEzMWQ5YmQ2ZjJhNTIwY2EyMTJjYWY1MDU0MGJhZGE5MWUyNzA0YmM1YmFhNmIzYzRkNjM3OTE4MWM1NjQiCiAgICB9CiAgfQp9", "JMgaRF67DJ7iYyrMaTWyGmYw36N7eQN+3fP7e94PxWIBX7thwvQz1OR2AkPtgUaPZAu/BoFGA4Cb9QDKkyWq/vic/H0cww79BOLt9atdm0/MUsljsLQB+r9ugIttFt1yTDSOmpUM1nObQYbC55rDB4b9rRpQZuYVXrM9Hot2mXLT7/QCqfAgdXtpz/C2aOUt6BMHDuqqMtRSq7fvxAaMeT1XKnVFukk5+PJXMM88lBIi7wmt6wkdiOO9aKur05EiYihXh6cxcWLFNv1dGYG1CLlcTILO3N4hluMNX77bSGPzADwA3kYUswxhsQGwV1M96cuY8mZJqFXBClTOKFgv8d/CqXVHUkDowNHWveaw4YQJd2cM2kcJDj0joZ4GFxmaz2/1UaNEvQV6iNzLu3w9vTcfrK50EPf0YCUTrsemQxvTy98UAUqOe8ZXzAa6BgIYPgOQEmd3QSZc4cOoRuPjwVvZ/dyxTIs55Z0i3B6iyEU6Ikp4hCe3Nhky5M8ni0mNd+Z+Drw+yTVIZhmeDMZ097223Rz4VLBNqMZHF+smG5RBkxipqVeLCOdqvdyEqSS9t0lDafuNWsmRh146s1t048/nX05MTHZACR4yUiu5IponwXgl6EZ6DpOAjv3FSOfliewRV2r4HyVe/CDSCnq5uzBXwHfiZL0CQM/IR5JTw0E=");

    @Getter @Setter
    private String username, skinName, value, signature;

    SkinLibrary(String username, String skinName, String value, String signature) {
        this.username = username;
        this.skinName = skinName;
        this.value = value;
        this.signature = signature;
    }

    public static SkinLibrary getByName(String skinName) {
        for (SkinLibrary skins : values()) {
            if (skins.getSkinName().equalsIgnoreCase(skinName)) {
                return skins;
            }
        }
        return null;
    }

    public static SkinLibrary getRandomSkin() {
        return values()[new Random().nextInt(values().length)];
    }
}
