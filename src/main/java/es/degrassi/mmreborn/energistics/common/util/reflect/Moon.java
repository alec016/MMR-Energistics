package es.degrassi.mmreborn.energistics.common.util.reflect;

import es.degrassi.mmreborn.common.util.MMRLogger;
import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@ApiStatus.Internal
public class Moon {
  private static final Unsafe UNSAFE = unsafe();
  private static final MethodHandles.Lookup LOOKUP = lookup();

  public Moon() {
  }

  private static Unsafe unsafe() {
    try {
      Field theSafe = Unsafe.class.getDeclaredField("theUnsafe");
      theSafe.setAccessible(true);
      return (Unsafe)theSafe.get(null);
    } catch (NoSuchFieldException | IllegalAccessException var1) {
      throw new RuntimeException(var1);
    }
  }

  private static MethodHandles.Lookup lookup() {
    try {
      Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      long offset = UNSAFE.staticFieldOffset(field);
      return (MethodHandles.Lookup)UNSAFE.getObject(MethodHandles.Lookup.class, offset);
    } catch (NoSuchFieldException var3) {
      throw new RuntimeException(var3);
    }
  }

  public static void removeFinal(Field field) {
    try {
      VarHandle m = LOOKUP.findVarHandle(Field.class, "modifiers", Integer.TYPE);
      int modify = field.getModifiers();
      m.set(field, modify & -17);
    } catch (IllegalAccessException | NoSuchFieldException var3) {
      ReflectiveOperationException e = var3;
      MMRLogger.INSTANCE.error(e.getMessage());
    }

  }

  public static void setField(Field field, Object owner, Object value) {
    if (Modifier.isStatic(field.getModifiers())) {
      putHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value);
    } else {
      putHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field), value);
    }

  }

  public static Object getField(Field field, Object owner) {
    return Modifier.isStatic(field.getModifiers()) ? getHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field)) : getHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field));
  }

  private static void putHelper(Class<?> clazz, Object owner, long offset, Object value) {
    if (clazz == Integer.TYPE) {
      UNSAFE.putInt(owner, offset, (Integer)value);
    } else if (clazz == Short.TYPE) {
      UNSAFE.putShort(owner, offset, (Short)value);
    } else if (clazz == Byte.TYPE) {
      UNSAFE.putByte(owner, offset, (Byte)value);
    } else if (clazz == Long.TYPE) {
      UNSAFE.putLong(owner, offset, (Long)value);
    } else if (clazz == Float.TYPE) {
      UNSAFE.putFloat(owner, offset, (Float)value);
    } else if (clazz == Double.TYPE) {
      UNSAFE.putDouble(owner, offset, (Double)value);
    } else if (clazz == Boolean.TYPE) {
      UNSAFE.putBoolean(owner, offset, (Boolean)value);
    } else if (clazz == Character.TYPE) {
      UNSAFE.putChar(owner, offset, (Character)value);
    } else {
      UNSAFE.putObject(owner, offset, value);
    }

  }

  private static Object getHelper(Class<?> clazz, Object owner, long offset) {
    if (clazz == Integer.TYPE) {
      return UNSAFE.getInt(owner, offset);
    } else if (clazz == Short.TYPE) {
      return UNSAFE.getShort(owner, offset);
    } else if (clazz == Byte.TYPE) {
      return UNSAFE.getByte(owner, offset);
    } else if (clazz == Long.TYPE) {
      return UNSAFE.getLong(owner, offset);
    } else if (clazz == Float.TYPE) {
      return UNSAFE.getFloat(owner, offset);
    } else if (clazz == Double.TYPE) {
      return UNSAFE.getDouble(owner, offset);
    } else if (clazz == Boolean.TYPE) {
      return UNSAFE.getBoolean(owner, offset);
    } else {
      return clazz == Character.TYPE ? UNSAFE.getChar(owner, offset) : UNSAFE.getObject(owner, offset);
    }
  }
}
