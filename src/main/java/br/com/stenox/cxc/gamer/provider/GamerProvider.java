package br.com.stenox.cxc.gamer.provider;

import br.com.stenox.cxc.gamer.Gamer;
import br.com.stenox.cxc.gamer.team.GamerTeam;
import br.com.stenox.cxc.kit.Kit;
import br.com.stenox.cxc.Main;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GamerProvider {

    private final Set<Gamer> gamers;
    private Main plugin;

    public GamerProvider(Main plugin){
        this.gamers = new HashSet<>();

        this.plugin = plugin;
    }

    public void create(Gamer model){
        this.gamers.add(model);
    }

    public Gamer search(UUID uniqueId){
        return this.gamers.stream().filter(p -> p.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    public Gamer search(String name){
        return this.gamers.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public Set<Gamer> getAliveGamers(){
        return gamers.stream().filter(gamer -> gamer.isAlive() && !gamer.isVanish()).collect(Collectors.toSet());
    }

    public void update(Gamer gamer){
        if (gamer != null){
            plugin.getGamerRepository().update(gamer);
        }
    }

    public void remove(UUID uniqueId){
        Gamer gamer = search(uniqueId);
        if (gamer != null){
            //update(gamer);
            gamers.remove(gamer);
        }
    }

    public boolean contains(UUID uniqueId){
        Gamer gamer = search(uniqueId);
        if (gamer != null) return true;

        return plugin.getGamerRepository().contains(uniqueId);
    }

    public void updatePlayers(){
        gamers.forEach(Gamer::updatePlayer);
    }

    public int getPlayersContForTeam(GamerTeam team){
        return (int) gamers.stream().filter(gamer -> gamer.getTeam() == team && gamer.isAlive() && !gamer.isVanish()).count();
    }

    public Kit getRandomKit() {
        ArrayList<Kit> possibleKits = new ArrayList<Kit>();
        for (Kit kits : Main.getInstance().getKitManager().getKits()) {
            if (kits.getName().equalsIgnoreCase("Nenhum"))
                continue;
            if (kits.getName().equalsIgnoreCase("Surprise"))
                continue;
            if (!kits.isActive())
                continue;

            possibleKits.add(kits);
        }
        return possibleKits.get(new Random().nextInt(possibleKits.size()));
    }
}
