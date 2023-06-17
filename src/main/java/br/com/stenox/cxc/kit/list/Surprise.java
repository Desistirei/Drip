package br.com.stenox.cxc.kit.list;

import br.com.stenox.cxc.game.manager.GameManager;
import br.com.stenox.cxc.kit.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Surprise extends Kit {

    public Surprise(GameManager gameManager) {
        super(gameManager);
        setIcon(new ItemStack(Material.CAKE));
        setDescription("§7Receba um kit totalmente aleatório.");
    }
}
