package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import static net.minecraft.block.Block.getStateId;

public class PlacerBlockTile extends SuckerBlockTile {

    static DirectionProperty[] dirProps = {
            BlockStateProperties.FACING,
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.FACING_EXCEPT_UP
    };

    public PlacerBlockTile() {
        super(ModBlocks.PLACER.tile);
    }

    @Override
    protected void onNoExtend(BlockPos pos) {
        postRetract(pos);
    }

    private static BlockState withFacing(BlockState state, Direction myDO) {
        for (DirectionProperty dp: dirProps) {
            if (dp.getAllowedValues().contains(myDO) && state.has(dp)) {
                return state.with(dp, myDO);
            }
        }
        return state;
    }

    protected boolean postRetract(BlockPos blockpos) {
        IItemHandler h = getHandler();
        if (h != null) {
            ItemStack stack = h.extractItem(0, 1, false);
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                BlockItem blockItem = ((BlockItem) item);
                Block block = blockItem.getBlock();
                BlockState newState = block.getDefaultState();
                if (newState.isValidPosition(world, blockpos)) {
                    newState = withFacing(newState, myDir().getOpposite());
                    world.setBlockState(blockpos, newState);
                    BlockItem.setTileEntityNBT(world, null, blockpos, stack);
                    block.onBlockPlacedBy(world, blockpos, newState, null, stack);
                    SoundType st = newState.getSoundType(world, blockpos, null);
                    world.playSound(null, blockpos, st.getPlaceSound(), SoundCategory.BLOCKS,
                            (st.getVolume() + 1.0F) / 2.0F, st.getPitch() * 0.8F);
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
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new PlacerBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
