package mod.command;

import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.List;

public class ModCommands {
	
	private static List<ICommand> commands = new ArrayList<ICommand>();
	
	static {
		commands.add(new CommandSurreal());
	}
	
	public static List<ICommand> getCommands() {
		return commands;
	}
}
