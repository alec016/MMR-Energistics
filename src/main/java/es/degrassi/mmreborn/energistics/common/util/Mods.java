package es.degrassi.mmreborn.energistics.common.util;

import net.neoforged.fml.ModList;

public interface Mods {

  private static boolean isLoaded(String modId) {
    return ModList.get() != null && ModList.get().isLoaded(modId);
  }

  static boolean isMekPossible() {
    return isMekLoaded() && isMMRMekLoaded() && isAppMekLoaded();
  }

  static boolean isArsPossible() {
    return isArsLoaded() && isMMRArsLoaded() && isAppArsLoaded();
  }

  static boolean isExperiencePossible() {
    return isExperienceLibLoaded() && isAppExLoaded();
  }

  static boolean isMekLoaded() {
    return isLoaded("mekanism");
  }

  static boolean isAppMekLoaded() {
    return isLoaded("appmek");
  }

  static boolean isMMRMekLoaded() {
    return isLoaded("modular_machinery_reborn_mekanism");
  }

  static boolean isArsLoaded() {
    return isLoaded("ars_nouveau");
  }

  static boolean isAppArsLoaded() {
    return isLoaded("arseng");
  }

  static boolean isMMRArsLoaded() {
    return isLoaded("modular_machinery_reborn_ars");
  }

  static boolean isExperienceLibLoaded() {
    return isLoaded("experiencelib");
  }

  static boolean isAppExLoaded() {
    return isLoaded("appex");
  }
}
