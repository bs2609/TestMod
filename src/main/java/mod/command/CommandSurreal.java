package mod.command;

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
	public String getCommandName() {
		return "surreal";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
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
			Teleporter teleporter = new SurrealWorldTeleporter(player.getServerWorld());
			server.getPlayerList().transferPlayerToDimension(player, ModDimensions.DIM_SURREAL, teleporter);
		}
	}
}
