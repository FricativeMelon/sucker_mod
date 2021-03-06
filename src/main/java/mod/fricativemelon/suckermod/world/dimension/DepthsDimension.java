package mod.fricativemelon.suckermod.world.dimension;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class DepthsDimension extends Dimension {
    private static final Vec3d field_227177_f_ = new Vec3d(0.2F, 0.03F, 0.03F);

    public DepthsDimension(World worldIn, DimensionType typeIn) {
        super(worldIn, typeIn, 0.1F);
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @OnlyIn(Dist.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return field_227177_f_;
    }

    public ChunkGenerator<?> createChunkGenerator() {
        NetherGenSettings nethergensettings = ChunkGeneratorType.CAVES.createSettings();
        nethergensettings.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
        nethergensettings.setDefaultFluid(Blocks.LAVA.getDefaultState());
        return ChunkGeneratorType.CAVES.create(this.world, BiomeProviderType.FIXED.create(BiomeProviderType.FIXED.func_226840_a_(this.world.getWorldInfo()).setBiome(Biomes.NETHER)), nethergensettings);
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    public boolean isSurfaceWorld() {
        return false;
    }

    @Nullable
    public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
        return null;
    }

    @Nullable
    public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
        return null;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.5F;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere() {
        return false;
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean doesXZShowFog(int x, int z) {
        return true;
    }

    public WorldBorder createWorldBorder() {
        return new WorldBorder() {
            public double getCenterX() {
                return super.getCenterX() / 8.0D;
            }

            public double getCenterZ() {
                return super.getCenterZ() / 8.0D;
            }
        };
    }

}
