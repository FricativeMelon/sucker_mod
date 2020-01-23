package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockContainer extends SuckerBlockContainer {
    public HarvesterBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player) {
        super(id, world, pos, inv, player, ModBlocks.HARVESTER.container);
    }

    public HarvesterBlockContainer(int id, BlockPos pos, PlayerInventory inv) {
        super(id, pos, inv, ModBlocks.HARVESTER.container);
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.HARVESTER.block;
    }
}
