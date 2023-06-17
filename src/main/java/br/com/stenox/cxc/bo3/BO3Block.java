package br.com.stenox.cxc.bo3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@AllArgsConstructor
public class BO3Block {

    @Getter
    private int x,y,z;
    @Getter
    private Material material;
    @Getter
    private byte data;
}
