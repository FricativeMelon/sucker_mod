package mod.fricativemelon.suckermod.utils;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.function.Supplier;

public class RegisteredTileContainerBlock<B extends Block, T extends TileEntity, C extends Container>
        extends RegisteredTileBlock<B, T> {

    public ContainerType<C> container;

    public RegisteredTileContainerBlock(String name) {
        super(name);
    }

    public void setContainer(final RegistryEvent.Register<ContainerType<?>> event, IContainerFactory<C> ifct) {
        this.container = IForgeContainerType.create(ifct);
        event.getRegistry().register(this.container.setRegistryName(name));
    }

}
