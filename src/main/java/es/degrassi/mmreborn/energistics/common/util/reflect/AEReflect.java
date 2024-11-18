package es.degrassi.mmreborn.energistics.common.util.reflect;

import appeng.api.networking.crafting.ICraftingLink;
import appeng.helpers.MultiCraftingTracker;

import java.lang.reflect.Method;

public class AEReflect {

  private static final Method craftingTrackerCancel;
  private static final Method craftingTrackerGetSlot;
  private static final Method craftingTrackerIsBusy;

  static {
    try {
      craftingTrackerCancel = ReflectKit.reflectMethod(Class.forName("appeng.helpers.MultiCraftingTracker"), "cancel");
      craftingTrackerCancel.setAccessible(true);
      craftingTrackerGetSlot = ReflectKit.reflectMethod(Class.forName("appeng.helpers.MultiCraftingTracker"),
          "getSlot", ICraftingLink.class);
      craftingTrackerGetSlot.setAccessible(true);
      craftingTrackerIsBusy = ReflectKit.reflectMethod(Class.forName("appeng.helpers.MultiCraftingTracker"),
          "isBusy", int.class);
      craftingTrackerIsBusy.setAccessible(true);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
    }
  }

  public static void cancel(MultiCraftingTracker tracker) {
    ReflectKit.executeMethod(tracker, craftingTrackerCancel);
  }

  public static int getSlot(MultiCraftingTracker tracker, ICraftingLink link) {
    return ReflectKit.executeMethod2(tracker, craftingTrackerGetSlot, link);
  }

  public static boolean isBusy(MultiCraftingTracker tracker, int slot) {
    return ReflectKit.executeMethod2(tracker, craftingTrackerIsBusy, slot);
  }
}
