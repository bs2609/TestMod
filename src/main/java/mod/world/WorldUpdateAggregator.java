package mod.world;

import mod.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class WorldUpdateAggregator {
	
	private final int dimension;
	
	private final Map<BlockPos, IBlockState> updates = new HashMap<BlockPos, IBlockState>();
	
	public WorldUpdateAggregator(int dimension) {
		this.dimension = dimension;
	}
	
	public void queueUpdate(BlockPos pos, IBlockState state) {
		updates.put(pos.toImmutable(), state);
		if (updates.size() >= 256) {
			applyUpdates();
		}
	}
	
	public void applyUpdates() {
		WorldServer world = MiscUtils.worldServerForDimension(dimension);
		for (Map.Entry<BlockPos, IBlockState> entry : updates.entrySet()) {
			BlockPos pos = entry.getKey();
			Chunk chunk = world.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);
			if (chunk != null) {
				chunk.setBlockState(pos, entry.getValue());
			}
		}
		updates.clear();
	}
}
