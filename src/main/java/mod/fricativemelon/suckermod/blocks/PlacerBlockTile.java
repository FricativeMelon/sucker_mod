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
        super(ModBlocks.PLACERBLOCK_TILE);
    }

    //places the block
    protected void setUpBlockTicks(BlockPos pos) {
        IItemHandler h = this.getHandler();
        if (h != null && world != null) {
            Direction myDir = world.getBlockState(this.pos).get(BlockStateProperties.FACING);
            ItemStack stack = h.getStackInSlot(0);
            stack = h.extractItem(0, stack.getMaxStackSize(), false);
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                int count = 1;
                while (count <= 12) {
                    switch (isPassable(world, pos, block)) {
                        case UNPLACEABLE:
                            count++;
                            pos = pos.offset(myDir);
                            break;
                        case PLACEABLE:
                            world.setBlockState(pos, block.getDefaultState());
                            stack.shrink(1);
                        case SOLID:
                            count = 13;
                    }
                }
            }
            h.insertItem(0, stack, false);
        }
    }

    @Override
    protected void powerChange(boolean rising) {
        if (rising) {
            setUpBlockTicks(getFacingPos());
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new PlacerBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
