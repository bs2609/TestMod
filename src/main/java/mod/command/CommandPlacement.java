package mod.command;

import mod.util.BlockArea;
import mod.util.BlockStateChecker;
import mod.util.BlockStructurePlacementHelper;
import mod.util.VectorUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommandPlacement extends CommandBase {
	
	private static final BlockStateChecker airBlocks = new BlockStateChecker() {
		@Override
		public boolean check(World world, BlockPos pos, IBlockState state) {
			return state.getBlock().isAir(state, world, pos);
		}
	};
	
	private static Vec3i parseSize(String[] args, int idx, int max) throws NumberInvalidException {
		int x = parseInt(args[idx  ], 0, max);
		int y = parseInt(args[idx+1], 0, max);
		int z = parseInt(args[idx+2], 0, max);
		return new Vec3i(x, y, z);
	}
	
	@Override
	public String getName() {
		return "placement";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 3) throw new WrongUsageException("");
		
		Vec3i size = parseSize(args, 0, 16);
		World world = sender.getEntityWorld();
		Vec3d location = sender.getPositionVector();
		BlockPos position = sender.getPosition();
		
		Vec3i offset = VectorUtils.add(VectorUtils.scale(size, 3), 2);
		BlockArea init = new BlockArea(position, position.add(offset));
		Vec3d v = location.subtract(init.getRelativePosition(new Vec3d(0.5, 0.0, 0.5)));
		BlockArea area = init.translate(new Vec3i(v.x, v.y, v.z));
		
		sender.sendMessage(new TextComponentString("Trying to place " + size + " within " + area));
		
		BlockStructurePlacementHelper helper = new BlockStructurePlacementHelper(world, area, airBlocks);
		Set<BlockArea> valid = helper.findPlacementsFor(size);
		
		sender.sendMessage(new TextComponentString("Found " + valid.size() + " valid placements"));
		
		if (!valid.isEmpty()) {
			SortedSet<BlockArea> sorted = new TreeSet<BlockArea>(BlockArea.compareDistancesTo(location));
			sorted.addAll(valid);
			sender.sendMessage(new TextComponentString("Best placement: " + sorted.first()));
		}
	}
}
