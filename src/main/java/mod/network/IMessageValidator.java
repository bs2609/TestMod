package mod.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IMessageValidator<T extends IMessage> {
	
	boolean validate(T message, MessageContext context);
}
