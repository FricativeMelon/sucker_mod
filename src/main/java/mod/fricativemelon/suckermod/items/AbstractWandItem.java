package mod.fricativemelon.suckermod.items;

import mod.fricativemelon.suckermod.SuckerMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Properties;

public abstract class AbstractWandItem extends Item {

    public AbstractWandItem(Properties props) {
        super(props);
    }

    protected abstract BlockState effect(World world, BlockPos pos, BlockState state);

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity playerentity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isRemote && playerentity != null) {
            BlockPos blockpos = context.getPos();
            BlockState state = effect(world, blockpos, world.getBlockState(blockpos));
            if (state != null && state.isValidPosition(world, blockpos)) {
                world.setBlockState(blockpos, state, 11);
                context.getItem().damageItem(1, playerentity, (p_219999_1_) -> {
                    p_219999_1_.sendBreakAnimation(context.getHand());
                });
            }
        }

        return ActionResultType.SUCCESS;
    }
}
