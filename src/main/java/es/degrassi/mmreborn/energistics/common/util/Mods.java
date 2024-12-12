package es.degrassi.mmreborn.energistics.common.util;

import net.neoforged.fml.ModList;

public interface Mods {
  ModList list = ModList.get();

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
    return list.isLoaded("mekanism");
  }

  static boolean isAppMekLoaded() {
    return list.isLoaded("appmek");
  }

  static boolean isMMRMekLoaded() {
    return list.isLoaded("modular_machinery_reborn_mekanism");
  }

  static boolean isArsLoaded() {
    return list.isLoaded("ars_nouveau");
  }

  static boolean isAppArsLoaded() {
    return list.isLoaded("arseng");
  }

  static boolean isMMRArsLoaded() {
    return list.isLoaded("modular_machinery_reborn_ars");
  }

  static boolean isExperienceLibLoaded() {
    return list.isLoaded("experiencelib");
  }

  static boolean isAppExLoaded() {
    return list.isLoaded("appex");
  }
}
