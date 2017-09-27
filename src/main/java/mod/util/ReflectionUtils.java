package mod.util;

import mod.TestMod;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
	
	public static class FieldAccessor {
		
		private final Field field;
		
		public FieldAccessor(Class<?> cls, String mcp, String srg) {
			field = ReflectionHelper.findField(cls, srg, mcp);
		}
		
		public Object get(Object obj) {
			try {
				return field.get(obj);
			} catch (Exception e) {
				TestMod.getLogger().error("Exception getting " + field, e);
				return null;
			}
		}
		
		public void set(Object obj, Object value) {
			try {
				field.set(obj, value);
			} catch (Exception e) {
				TestMod.getLogger().error("Exception setting " + field, e);
			}
		}
	}
	
	public static class MethodAccessor {
	
		private final Method method;
		
		public MethodAccessor(Class<?> cls, String mcp, String srg, Class<?>... params) {
			method = ReflectionHelper.findMethod(cls, mcp, srg, params);
		}
		
		public Object invoke(Object obj, Object... args) {
			try {
				return method.invoke(obj, args);
			} catch (Exception e) {
				TestMod.getLogger().error("Exception invoking " + method, e);
				return null;
			}
		}
	}
}
