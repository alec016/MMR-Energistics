package es.degrassi.mmreborn.energistics.api.services.crafting;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.google.common.primitives.Ints;
import es.degrassi.appexp.me.key.AEExperienceKey;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.MachineComponent;
import es.degrassi.mmreborn.common.manager.handler.slot.HybridTank;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import gripe._90.arseng.me.key.SourceKey;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class PatternBusTargetCache {

  private final MachineControllerEntity controller;
  private final PatternBusLogicHost entity;
  private final BlockPos pos;

  public PatternBusTargetCache(ServerLevel l, BlockPos pos, PatternBusLogicHost entity) {
    this.controller = (MachineControllerEntity) l.getBlockEntity(pos);
    this.entity = entity;
    this.pos = pos;
  }

  @Nullable
  public PatternBusTarget find() {
    if (controller == null) return null;
    var manager = controller.getComponentManager();
    return new PatternBusTarget() {
      @Override
      public BlockPos pos() {
        return pos;
      }

      @Override
      public Direction getSide() {
        return entity.getFront();
      }

      @Override
      public long insert(AEKey what, long amount, Actionable type) {
        var toReturn = new AtomicLong(0);
        switch (what) {
          case AEItemKey item -> {
            manager.getComponent(ComponentRegistration.COMPONENT_ITEM.get(), IOType.INPUT)
                .map(MachineComponent::getContainerProvider)
                .ifPresent(handler -> {
                  var remaining = handler.insertItem(item.toStack(Ints.saturatedCast(amount)), type.isSimulate()).getCount();
                  toReturn.addAndGet(amount - remaining);
                });
          }
          case AEFluidKey fluid -> {
            manager.getComponent(ComponentRegistration.COMPONENT_FLUID.get(), IOType.INPUT)
                .map(MachineComponent::getContainerProvider)
                .ifPresent(handler -> {
                  var remaining = handler.fill(fluid.toStack(Ints.saturatedCast(amount)), type.getFluidAction());
                  toReturn.addAndGet(amount - remaining);
                });
          }
          case AEExperienceKey experience -> {
            manager.getComponent(ComponentRegistration.COMPONENT_EXPERIENCE.get(), IOType.INPUT)
                .map(MachineComponent::getContainerProvider)
                .ifPresent(handler -> {
                  long toInsert = amount;
                  long inserted;
                  for (long i = 0; i < handler.getTanks() && toInsert > 0; i++,
                      toInsert -= inserted) {
                    inserted = handler.receiveExperienceRecipe(Ints.saturatedCast(i), toInsert, type.isSimulate());
                    toReturn.addAndGet(inserted);
                  }
                });
          }
          case SourceKey source -> {
            manager.getComponent(es.degrassi.mmreborn.ars.common.registration.ComponentRegistration.COMPONENT_SOURCE.get(), IOType.INPUT)
                .map(MachineComponent::getContainerProvider)
                .ifPresent(handler -> {
                  var inserted = handler.receiveSource(Ints.saturatedCast(amount), type.isSimulate());
                  toReturn.addAndGet(inserted);
                });
          }
          case MekanismKey chemical -> {
            manager.getComponent(es.degrassi.mmreborn.mekanism.common.registration.ComponentRegistration.COMPONENT_CHEMICAL.get(), IOType.INPUT)
                .map(MachineComponent::getContainerProvider)
                .ifPresent(handler -> {
                  var remaining = handler.insertChemical(chemical.withAmount(amount), Action.fromFluidAction(type.getFluidAction())).getAmount();
                  toReturn.addAndGet(amount - remaining);
                });
          }
          default -> {
          }
        }
        return toReturn.get();
      }

      @Override
      public boolean containsPatternInput(Set<AEKey> patternInputs) {
        var manager = controller.getComponentManager();
        for (var key : patternInputs) {
          AtomicBoolean contains = new AtomicBoolean(false);
          switch (key) {
            case AEItemKey item -> {
              manager.getComponent(ComponentRegistration.COMPONENT_ITEM.get(), IOType.INPUT)
                  .map(MachineComponent::getContainerProvider)
                  .ifPresent(handler -> {
                    contains.set(handler.extractItem(item.toStack(), true).isEmpty());
                  });
            }
            case AEFluidKey fluid -> {
              manager.getComponent(ComponentRegistration.COMPONENT_FLUID.get(), IOType.INPUT)
                  .map(MachineComponent::getContainerProvider)
                  .ifPresent(handler -> {
                    contains.set(handler.getInventory().stream().map(HybridTank::getFluid).anyMatch(fluid::matches));
                  });
            }
            case AEExperienceKey experience -> {
              manager.getComponent(ComponentRegistration.COMPONENT_EXPERIENCE.get(), IOType.INPUT)
                  .map(MachineComponent::getContainerProvider)
                  .ifPresent(handler -> {
                    contains.set(handler.getExperience() > 0);
                  });
            }
            case MekanismKey chemical -> {
              manager.getComponent(es.degrassi.mmreborn.mekanism.common.registration.ComponentRegistration.COMPONENT_CHEMICAL.get(), IOType.INPUT)
                  .map(MachineComponent::getContainerProvider)
                  .ifPresent(handler -> {
                    contains.set(chemical.getStack().is(handler.getTypeHolder()));
                  });
            }
            case SourceKey source -> {
              manager.getComponent(es.degrassi.mmreborn.ars.common.registration.ComponentRegistration.COMPONENT_SOURCE.get(), IOType.INPUT)
                  .map(MachineComponent::getContainerProvider)
                  .ifPresent(handler -> {
                    contains.set(handler.getSource() > 0);
                  });
            }
            default -> {
            }
          }
          if (contains.get()) return true;
        }
        return false;
      }
    };
  }
}
