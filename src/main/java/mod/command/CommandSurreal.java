package mod.command;

import mod.util.MiscUtils;
import mod.world.ModDimensions;
import mod.world.SurrealWorldTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Teleporter;

public class CommandSurreal extends CommandBase {
	
	@Override
	public String getName() {
		return "surreal";
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
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			int destination = ModDimensions.DIM_SURREAL;
			Teleporter teleporter = new SurrealWorldTeleporter(server.getWorld(destination));
			MiscUtils.changeDimension(player, destination, teleporter);
		}
	}
}
