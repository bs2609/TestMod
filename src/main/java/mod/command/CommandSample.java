package mod.command;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;

public class CommandSample extends CommandBase {
	
	@Override
	public String getName() {
		return "sample";
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
		
		Chunk chunk = sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition());
		Multiset<IBlockState> states = HashMultiset.create();
		
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				for (int y = 0; y < 256; ++y) {
					states.add(chunk.getBlockState(x, y, z));
				}
			}
		}
		
		String msg = "Chunk [" + chunk.xPosition + ", " + chunk.zPosition + "] contains " + states.elementSet().size() + " blockstates";
		sender.sendMessage(new TextComponentString(msg));
	}
}
