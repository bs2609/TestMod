package mod.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SimpleBlockAccess implements IBlockAccess {
	
	protected IBlockState defaultState = Blocks.AIR.getDefaultState();
	
	protected Biome defaultBiome = Biomes.DEFAULT;
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getCombinedLight(BlockPos pos, int lightValue) {
		int skyLight = getLightFor(EnumSkyBlock.SKY, pos);
		int blockLight = Math.max(getLightFor(EnumSkyBlock.BLOCK, pos), lightValue);
		return skyLight << 20 | blockLight << 4;
	}
	
	public int getLightFor(EnumSkyBlock type, BlockPos pos) {
		if (type == EnumSkyBlock.SKY && getWorld().provider.getHasNoSky()) return 0;
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
		return (chunk != null) ? chunk.getBiome(pos, getWorld().getBiomeProvider()) : defaultBiome;
	}
	
	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return getBlockState(pos).getStrongPower(this, pos, direction);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public WorldType getWorldType() {
		return getWorld().getWorldType();
	}
	
	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		if (!checkHeight(pos)) return _default;
		Chunk chunk = getChunk(pos);
		return (chunk != null) ? chunk.getBlockState(pos).isSideSolid(this, pos, side) : _default;
	}
	
	protected Chunk getChunk(BlockPos pos) {
		return getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
	
	protected abstract Chunk getChunk(int x, int z);
	
	protected abstract World getWorld();
}
