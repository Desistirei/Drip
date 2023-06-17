package br.com.stenox.cxc.game;

import br.com.stenox.cxc.event.game.GameChangeStageEvent;
import br.com.stenox.cxc.utils.Formatter;
import br.com.stenox.cxc.variable.Variable;
import br.com.stenox.cxc.Main;
import br.com.stenox.cxc.game.mode.GameMode;
import br.com.stenox.cxc.game.stage.GameStage;
import br.com.stenox.cxc.game.timer.GameTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
public class Game {

    private GameMode mode;

    private GameStage stage;

    private GameTimer timer;
    private int time;

    private boolean running;

    public Game(GameMode mode){
        this.mode = mode;

        setStage(GameStage.STARTING);
        running = false;

        timer = new GameTimer(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), timer, 0L, 20L);
    }

    public void setStage(GameStage newStage) {
        if (newStage == GameStage.ENDING)
            running = false;
        else
            this.time = (mode == GameMode.CLANXCLAN ? newStage.getStartTimeCxC() : newStage.getStartTimeEvent());

        if (newStage == GameStage.INVINCIBILITY)
            this.time = (int) Variable.TIMINGS_INVINCIBILITY.getValue();
        else if (newStage == GameStage.IN_GAME)
            this.time = Main.LAST_INVINCIBILITY_TIME;

        new GameChangeStageEvent(stage, newStage).call();

        stage = newStage;
    }

    public String getFormattedTime(){
        return Formatter.toTime(time);
    }

    public String getFormattedLongTime(){
        return Formatter.toTimeLong(time);
    }

    public void moveTime(){
        if (stage.isIncrement())
            time++;
        else
            time--;
    }

    public void startTimer(){
        running = true;
    }

    public void stopTimer(){
        running = false;
    }
}
