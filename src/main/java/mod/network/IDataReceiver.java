package mod.network;

public interface IDataReceiver<T> {
	
	void accept(T t);
}
