package mod.fricativemelon.suckermod.blocks;

import mod.fricativemelon.suckermod.SuckerMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class SuckerBlockContainer extends Container {

	private TileEntity tileEntity;
	private PlayerEntity playerEntity;
	private IItemHandler playerInventory;
	
	public SuckerBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player,
                                ContainerType<?> ct) {
		super(ct, id);
		this.tileEntity = world.getTileEntity(pos);
		this.playerEntity = player;
		this.playerInventory = new InvWrapper(inv);
		this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 64, 24));
            addSlot(new SlotItemHandler(h, 1, 100, 24));
        });
        layoutPlayerInventorySlots(10, 70);
	}

    public SuckerBlockContainer(int id, BlockPos pos, PlayerInventory inv, ContainerType<?> ct) {
        this(id, SuckerMod.proxy.getClientWorld(), pos, inv, SuckerMod.proxy.getClientPlayer(), ct);
    }

	protected abstract Block getBlock();

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()),
				playerEntity, getBlock());
	}
	
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index < 2) {
                if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (SuckerBlockTile.isStackAcceptable(index, stack)) {
                    if (!this.mergeItemStack(stack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 29) {
                    if (!this.mergeItemStack(stack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.mergeItemStack(stack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

}
