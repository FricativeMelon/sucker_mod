package mod.fricativemelon.suckermod.blocks;

import mod.fricativemelon.suckermod.SuckerMod;
import net.minecraft.block.Block;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.IContainerFactory;

public class PlacerBlockContainer extends SuckerBlockContainer {
    public PlacerBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player) {
        super(id, world, pos, inv, player, ModBlocks.PLACER.container);
    }

    public PlacerBlockContainer(int id, BlockPos pos, PlayerInventory inv) {
        super(id, pos, inv, ModBlocks.PLACER.container);
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.PLACER.block;
    }
}
