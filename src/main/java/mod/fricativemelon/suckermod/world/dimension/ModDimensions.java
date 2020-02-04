package mod.fricativemelon.suckermod.world.dimension;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ModDimensions {

    public static DimensionType DEPTHS;

    public static void init() {
        ModDimension md = ModDimension.withFactory(DepthsDimension::new);
        ResourceLocation rsl = new ResourceLocation("suckermod:depths");
        DEPTHS = DimensionManager.registerDimension(rsl, md, new PacketBuffer(Unpooled.buffer()), false);
    }

}
