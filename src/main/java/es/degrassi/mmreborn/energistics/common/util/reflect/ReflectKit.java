package es.degrassi.mmreborn.energistics.common.util.reflect;

import es.degrassi.mmreborn.common.util.MMRLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectKit {
  public ReflectKit() {
  }

  public static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
    return reflectMethod(owner, new String[]{name}, paramTypes);
  }

  public static Method reflectMethod(Class<?> owner, String[] names, Class<?>... paramTypes) throws NoSuchMethodException {
    Method m = null;

    for (String name : names) {
      try {
        m = owner.getDeclaredMethod(name, paramTypes);
        break;
      } catch (NoSuchMethodException ignored) {
      }
    }

    if (m == null) {
      throw new NoSuchMethodException("Can't find field from " + Arrays.toString(names));
    } else {
      m.setAccessible(true);
      return m;
    }
  }

  public static Field reflectField(Class<?> owner, String... names) throws NoSuchFieldException {
    Field f = null;

    for (String name : names) {
      try {
        f = owner.getDeclaredField(name);
        break;
      } catch (NoSuchFieldException ignored) {
      }
    }

    if (f == null) {
      throw new NoSuchFieldException("Can't find field from " + Arrays.toString(names));
    } else {
      removeFinal(f);
      f.setAccessible(true);
      return f;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T readField(Object owner, Field field) {
    try {
      return (T) Moon.getField(field, owner);
    } catch (Exception var3) {
      MMRLogger.INSTANCE.error("Reflect error.", var3);
      throw new IllegalStateException("Failed to read field: " + field);
    }
  }

  public static void writeField(Object owner, Field field, Object value) {
    try {
      Moon.setField(field, owner, value);
    } catch (Exception var4) {
      MMRLogger.INSTANCE.error("Reflect error.", var4);
      throw new IllegalStateException("Failed to write field: " + field);
    }
  }

  public static void executeMethod(Object owner, Method method, Object... args) {
    try {
      method.invoke(owner, args);
    } catch (InvocationTargetException | IllegalAccessException var4) {
      MMRLogger.INSTANCE.error("Reflect error.", var4);
      throw new IllegalStateException("Failed to execute method: " + method);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T executeMethod2(Object owner, Method method, Object... args) {
    try {
      return (T) method.invoke(owner, args);
    } catch (InvocationTargetException | IllegalAccessException var4) {
      MMRLogger.INSTANCE.error("Reflect error.", var4);
      throw new IllegalStateException("Failed to execute method: " + method);
    }
  }

  private static void removeFinal(Field field) {
    int modify = field.getModifiers();
    if (!field.getType().isPrimitive() || !Modifier.isFinal(modify)) {
      if (Modifier.isStatic(modify) && Modifier.isFinal(modify)) {
        Moon.removeFinal(field);
      }

    }
  }
}
