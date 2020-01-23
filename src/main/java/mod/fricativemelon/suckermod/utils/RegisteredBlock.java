package mod.fricativemelon.suckermod.utils;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

import java.util.function.Supplier;

public class RegisteredBlock<B extends Block> {

    public String name;
    public B block;

    public RegisteredBlock(String name) {
        this.name = name;
    }

    public void setBlock(final RegistryEvent.Register<Block> event, Supplier<B> blockSupply) {
        this.block = blockSupply.get();
        event.getRegistry().register(this.block.setRegistryName(name));
    }

    public void setItem(final RegistryEvent.Register<Item> event, Item.Properties properties) {
        event.getRegistry().register(new BlockItem(this.block, properties)
                .setRegistryName(name));
    }

}
