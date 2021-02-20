/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.machinesv2.init;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.SlotPositions;
import aztech.modern_industrialization.machines.impl.MachineTier;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import aztech.modern_industrialization.machinesv2.MachineBlockEntity;
import aztech.modern_industrialization.machinesv2.blockentities.ElectricMachineBlockEntity;
import aztech.modern_industrialization.machinesv2.blockentities.SteamMachineBlockEntity;
import aztech.modern_industrialization.machinesv2.components.MachineInventoryComponent;
import aztech.modern_industrialization.machinesv2.components.sync.EnergyBar;
import aztech.modern_industrialization.machinesv2.components.sync.ProgressBar;
import aztech.modern_industrialization.machinesv2.components.sync.RecipeEfficiencyBar;
import aztech.modern_industrialization.machinesv2.gui.MachineGuiParameters;
import aztech.modern_industrialization.machinesv2.models.MachineModels;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.TranslatableText;

/**
 * Registration of all single block crafting machines.
 */
public final class SingleBlockCraftingMachines {
    public static void init() {
        // @formatter:off // TODO: figure out why this doesn't work and remove the exclude from the build.gradle
        registerMachineTiers(
                "assembler", MIMachineRecipeTypes.ASSEMBLER, 9, 3, 1, 0,
                guiParams -> guiParams.backgroundHeight(186),
                new ProgressBar.Parameters(102, 43, "circuit"), new RecipeEfficiencyBar.Parameters(43, 86), new EnergyBar.Parameters(14, 44),
                items -> items.addSlots(42, 27, 3, 3).addSlots(129, 27, 1, 3), fluids -> fluids.addSlot(98, 27),
                true, true, false,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "centrifuge", MIMachineRecipeTypes.CENTRIFUGE, 1, 4, 1, 4, guiParams -> {},
                new ProgressBar.Parameters(65, 33, "centrifuge"), new RecipeEfficiencyBar.Parameters(50, 66), DEFAULT_ENERGY_BAR,
                items -> items.addSlot(42, 27).addSlots(93, 27, 2, 2), fluids -> fluids.addSlot(42, 45).addSlots(131, 27, 2, 2),
                true, true, true,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "chemical_reactor", MIMachineRecipeTypes.CHEMICAL_REACTOR, 3, 3, 3, 3, guiParams -> {},
                new ProgressBar.Parameters(88, 37, "triple_arrow"), new RecipeEfficiencyBar.Parameters(50, 66), new EnergyBar.Parameters(12, 35),
                items -> items.addSlots(30, 27, 3, 1).addSlots(116, 27, 3, 1), fluids -> fluids.addSlots(30, 47, 3, 1).addSlots(116, 47, 3, 1),
                true, false, false,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "compressor", MIMachineRecipeTypes.COMPRESSOR, 1, 1, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "compress"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(18, 30),
                items -> items.addSlot(56, 32).addSlot(102, 32), fluids -> {},
                true, true, true,
                TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "cutting_machine", MIMachineRecipeTypes.CUTTING_MACHINE, 1, 1, 1, 0, guiParams -> {},
                new ProgressBar.Parameters(88, 31, "slice"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(15, 34),
                items -> items.addSlot(60, 35).addSlot(120, 35), fluids -> fluids.addSlot(40, 35),
                true, false, false,
                TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "distillery", MIMachineRecipeTypes.DISTILLERY, 0, 0, 1, 1, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "arrow"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(18, 30),
                items -> {}, fluids -> fluids.addSlot(56, 35).addSlot(102, 35),
                true, false, false,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "electrolyzer", MIMachineRecipeTypes.ELECTROLYZER, 1, 4, 1, 4, guiParams -> {},
                new ProgressBar.Parameters(66, 35, "arrow"), new RecipeEfficiencyBar.Parameters(50, 66), DEFAULT_ENERGY_BAR,
                items -> items.addSlot(42, 27).addSlots(93, 27, 2, 2), fluids -> fluids.addSlot(42, 47).addSlots(131, 27, 2, 2),
                true, false, true,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "furnace", MIMachineRecipeTypes.FURNACE, 1, 1, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "arrow"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(18, 30),
                items -> items.addSlot(56, 32).addSlot(102, 32), fluids -> {},
                true, false, false,
                TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "macerator", MIMachineRecipeTypes.MACERATOR, 1, 4, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 33, "macerate"), new RecipeEfficiencyBar.Parameters(38, 66), DEFAULT_ENERGY_BAR,
                items -> items.addSlot(56, 35).addSlots(102, 27, 2, 2), fluids -> {},
                true, true, false,
                TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "mixer", MIMachineRecipeTypes.MIXER, 4, 2, 2, 2, guiParams -> {},
                new ProgressBar.Parameters(103, 33, "arrow"), new RecipeEfficiencyBar.Parameters(50, 66), new EnergyBar.Parameters(15, 34),
                items -> items.addSlots(62, 27, 2, 2).addSlots(129, 27, 1, 2), fluids -> fluids.addSlots(42, 27, 1, 2).addSlots(149, 27, 1, 2),
                true, true, true,
                TIER_BRONZE | TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "packer", MIMachineRecipeTypes.PACKER, 2, 2, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "arrow"), new RecipeEfficiencyBar.Parameters(38, 66), new EnergyBar.Parameters(18, 30),
                items -> items.addSlots(56, 27, 1, 2).addSlots(102, 27, 1, 2), fluids -> {},
                true, false, false,
                TIER_STEEL | TIER_ELECTRIC
        );
        registerMachineTiers(
                "polarizer", MIMachineRecipeTypes.POLARIZER, 1, 1, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "magnet"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(18, 30),
                items -> items.addSlot(56, 32).addSlot(102, 32), fluids -> {},
                true, true, false,
                TIER_ELECTRIC
        );
        registerMachineTiers(
                "wiremill", MIMachineRecipeTypes.WIREMILL, 1, 1, 0, 0, guiParams -> {},
                new ProgressBar.Parameters(77, 31, "wiremill"), new RecipeEfficiencyBar.Parameters(38, 62), new EnergyBar.Parameters(18, 30),
                items -> items.addSlot(56, 32).addSlot(102, 32), fluids -> {},
                true, true, false,
                TIER_STEEL | TIER_ELECTRIC
        );
        // @formatter:on
    }

    private static final EnergyBar.Parameters DEFAULT_ENERGY_BAR = new EnergyBar.Parameters(18, 34);

    public static void registerMachineTiers(String machine, MachineRecipeType type, int itemInputCount, int itemOutputCount, int fluidInputCount,
            int fluidOutputCount, Consumer<MachineGuiParameters.Builder> guiParams, ProgressBar.Parameters progressBarParams,
            RecipeEfficiencyBar.Parameters efficiencyBarParams, EnergyBar.Parameters energyBarParams, Consumer<SlotPositions.Builder> itemPositions,
            Consumer<SlotPositions.Builder> fluidPositions, boolean frontOverlay, boolean topOverlay, boolean sideOverlay, int tiers) {
        for (int i = 0; i < 2; ++i) {
            if (i == 0 && (tiers & TIER_BRONZE) == 0) {
                continue;
            }
            if (i == 1 && (tiers & TIER_STEEL) == 0) {
                continue;
            }

            SlotPositions.Builder itemPositionsBuilder = new SlotPositions.Builder();
            itemPositions.accept(itemPositionsBuilder);
            SlotPositions.Builder fluidPositionsBuilder = new SlotPositions.Builder();
            fluidPositionsBuilder.addSlot(12, 35);
            fluidPositions.accept(fluidPositionsBuilder);
            MachineTier tier = i == 0 ? MachineTier.BRONZE : MachineTier.STEEL;
            String prefix = i == 0 ? "bronze" : "steel";
            int steamBuckets = i == 0 ? 2 : 4;
            MachineGuiParameters.Builder guiParamsBuilder = new MachineGuiParameters.Builder(
                    new TranslatableText("block.modern_industrialization." + prefix + "_" + machine), true);
            guiParams.accept(guiParamsBuilder);
            MachineGuiParameters builtGuiParams = guiParamsBuilder.build();
            MachineRegistrationHelper.registerMachine(prefix + "_" + machine,
                    bet -> new SteamMachineBlockEntity(
                            bet, type, buildComponent(itemInputCount, itemOutputCount, fluidInputCount, fluidOutputCount,
                                    itemPositionsBuilder.build(), fluidPositionsBuilder.build(), steamBuckets),
                            builtGuiParams, progressBarParams, tier),
                    bet -> {
                        if (itemInputCount + itemOutputCount > 0) {
                            MachineBlockEntity.registerItemApi(bet);
                        }
                        MachineBlockEntity.registerFluidApi(bet);
                    });
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                MachineModels.addTieredMachine(prefix, machine, frontOverlay, topOverlay, sideOverlay);
            }
        }
        if ((tiers & TIER_ELECTRIC) > 0) {
            SlotPositions.Builder itemPositionsBuilder = new SlotPositions.Builder();
            itemPositions.accept(itemPositionsBuilder);
            SlotPositions.Builder fluidPositionsBuilder = new SlotPositions.Builder();
            fluidPositions.accept(fluidPositionsBuilder);
            MachineGuiParameters.Builder guiParamsBuilder = new MachineGuiParameters.Builder(
                    new TranslatableText("block.modern_industrialization.lv_" + machine), true);
            guiParams.accept(guiParamsBuilder);
            MachineGuiParameters builtGuiParams = guiParamsBuilder.build();
            MachineRegistrationHelper.registerMachine("lv_" + machine,
                    bet -> new ElectricMachineBlockEntity(bet, type,
                            buildComponent(itemInputCount, itemOutputCount, fluidInputCount, fluidOutputCount, itemPositionsBuilder.build(),
                                    fluidPositionsBuilder.build(), 0),
                            builtGuiParams, energyBarParams, progressBarParams, efficiencyBarParams, MachineTier.LV, 3200),
                    bet -> {
                        ElectricMachineBlockEntity.registerEnergyApi(bet);
                        if (itemInputCount + itemOutputCount > 0) {
                            MachineBlockEntity.registerItemApi(bet);
                        }
                        if (fluidInputCount + fluidOutputCount > 0) {
                            MachineBlockEntity.registerFluidApi(bet);
                        }
                    });
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                MachineModels.addTieredMachine("lv", machine, frontOverlay, topOverlay, sideOverlay);
            }
        }
    }

    private static final int TIER_BRONZE = 1, TIER_STEEL = 2, TIER_ELECTRIC = 4;

    /**
     * @param steamBuckets Number of steam buckets in the steam input slot, or 0 for
     *                     no steam input slot
     */
    private static MachineInventoryComponent buildComponent(int itemInputCount, int itemOutputCount, int fluidInputCount, int fluidOutputCount,
            SlotPositions itemPositions, SlotPositions fluidPositions, int steamBuckets) {
        int bucketCapacity = 16;

        List<ConfigurableItemStack> itemInputStacks = new ArrayList<>();
        for (int i = 0; i < itemInputCount; ++i) {
            itemInputStacks.add(ConfigurableItemStack.standardInputSlot());
        }
        List<ConfigurableItemStack> itemOutputStacks = new ArrayList<>();
        for (int i = 0; i < itemOutputCount; ++i) {
            itemOutputStacks.add(ConfigurableItemStack.standardOutputSlot());
        }
        List<ConfigurableFluidStack> fluidInputStacks = new ArrayList<>();
        if (steamBuckets > 0) {
            fluidInputStacks.add(ConfigurableFluidStack.lockedInputSlot(81000 * steamBuckets, MIFluids.STEAM));
        }
        for (int i = 0; i < fluidInputCount; ++i) {
            fluidInputStacks.add(ConfigurableFluidStack.standardInputSlot(81000 * bucketCapacity));
        }
        List<ConfigurableFluidStack> fluidOutputStacks = new ArrayList<>();
        for (int i = 0; i < fluidOutputCount; ++i) {
            fluidOutputStacks.add(ConfigurableFluidStack.standardOutputSlot(81000 * bucketCapacity));
        }

        return new MachineInventoryComponent(itemInputStacks, itemOutputStacks, fluidInputStacks, fluidOutputStacks, itemPositions, fluidPositions);
    }

    private SingleBlockCraftingMachines() {
    }
}
