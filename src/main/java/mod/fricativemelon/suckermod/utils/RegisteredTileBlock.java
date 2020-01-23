package mod.fricativemelon.suckermod.utils;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;

import java.util.function.Supplier;

public class RegisteredTileBlock<B extends Block, T extends TileEntity> extends RegisteredBlock<B> {

    public TileEntityType<T> tile;

    public RegisteredTileBlock(String name) {
        super(name);
    }

    public void setTile(final RegistryEvent.Register<TileEntityType<?>> event, Supplier<T> tileSupply) {
        this.tile = TileEntityType.Builder.create(tileSupply, block)
                .build(null);
        event.getRegistry().register(this.tile.setRegistryName(name));
    }

}
