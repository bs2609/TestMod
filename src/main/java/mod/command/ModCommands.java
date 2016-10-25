package mod.command;

import net.minecraft.command.ICommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModCommands {
	
	private static List<ICommand> commands = new ArrayList<ICommand>();
	
	static {
		commands.add(new CommandSample());
		commands.add(new CommandSurreal());
	}
	
	public static Collection<ICommand> getCommands() {
		return commands;
	}
}
