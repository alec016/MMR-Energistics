package es.degrassi.mmreborn.energistics.common.util;

import net.neoforged.fml.ModList;

public interface Mods {
  ModList list = ModList.get();

  static boolean isMekPossible() {
    return isMekLoaded() && isMMRMekLoaded() && isAppMekLoaded();
  }

  static boolean isMekLoaded() {
    return list.isLoaded("mekanism");
  }

  static boolean isAppMekLoaded() {
    return list.isLoaded("appmek");
  }

  static boolean isMMRMekLoaded() {
    return list.isLoaded("modular_machinery_reborn_mekanism");
  }
}
