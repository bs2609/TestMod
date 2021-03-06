package mod.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SimpleBlockAccess implements IBlockAccess {
	
	protected final IBlockState defaultState = Blocks.AIR.getDefaultState();
	protected final Biome defaultBiome = Biomes.DEFAULT;
	
	private final BiomeProvider defaultProvider = new BiomeProviderSingle(defaultBiome);
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
	}
	
	public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType mode) {
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getTileEntity(pos, mode) : null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getCombinedLight(BlockPos pos, int lightValue) {
		int skyLight = getLightFor(EnumSkyBlock.SKY, pos);
		int blockLight = Math.max(getLightFor(EnumSkyBlock.BLOCK, pos), lightValue);
		return skyLight << 20 | blockLight << 4;
	}
	
	public int getLightFor(EnumSkyBlock type, BlockPos pos) {
		if (checkHeight(pos)) {
			Chunk chunk = getChunk(pos);
			if (chunk != null) return chunk.getLightFor(type, pos);
		}
		return type.defaultLightValue;
	}
	
	private boolean checkHeight(BlockPos pos) {
		return pos.getY() >= 0 && pos.getY() < 256;
	}
	
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getBlockState(pos) : defaultState;
	}
	
	@Override
	public boolean isAirBlock(BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Biome getBiome(BlockPos pos) {
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getBiome(pos, getBiomeProvider()) : defaultBiome;
	}
	
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return getBlockState(pos).getStrongPower(this, pos, direction);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		if (!checkHeight(pos)) return _default;
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getBlockState(pos).isSideSolid(this, pos, side) : _default;
	}
	
	protected BiomeProvider getBiomeProvider() {
		return defaultProvider;
	}
	
	protected Chunk getChunk(BlockPos pos) {
		return getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
	
	protected abstract Chunk getChunk(int x, int z);
}
