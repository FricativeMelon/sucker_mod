package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import static net.minecraft.block.Block.getStateId;

public class PlacerBlockTile extends SuckerBlockTile {

    public PlacerBlockTile() {
        super(ModBlocks.PLACER.tile);
    }

    @Override
    protected void onNoExtend(BlockPos pos) {
        postRetract(pos);
    }

    protected boolean postRetract(BlockPos blockpos) {
        IItemHandler h = getHandler();
        if (h != null) {
            ItemStack stack = h.extractItem(0, 1, false);
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block.getDefaultState().isValidPosition(world, blockpos)) {
                    world.setBlockState(blockpos, block.getDefaultState());
                    return true;
                }
            } else {
                h.insertItem(0, stack, false);
            }
            this.markDirty();
        }
        return false;
    }

    @Override
    protected void powerChange(boolean rising) {
        if (rising) {
            setUpBlockTicks(getFacingPos());
        }
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new PlacerBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
