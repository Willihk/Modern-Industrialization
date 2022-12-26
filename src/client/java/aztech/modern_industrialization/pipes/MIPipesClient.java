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
package aztech.modern_industrialization.pipes;

import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;
import aztech.modern_industrialization.pipes.api.PipeRenderer;
import aztech.modern_industrialization.pipes.fluid.FluidPipeScreen;
import aztech.modern_industrialization.pipes.impl.ClientPipePackets;
import aztech.modern_industrialization.pipes.impl.PipeBlock;
import aztech.modern_industrialization.pipes.impl.PipeColorProvider;
import aztech.modern_industrialization.pipes.impl.PipeMeshCache;
import aztech.modern_industrialization.pipes.impl.PipeModelProvider;
import aztech.modern_industrialization.pipes.impl.PipePackets;
import aztech.modern_industrialization.pipes.item.ItemPipeScreen;
import aztech.modern_industrialization.util.RenderHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MIPipesClient {
    public void setupClient() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new PipeModelProvider());
        ColorProviderRegistry.BLOCK.register(new PipeColorProvider(), MIPipes.BLOCK_PIPE);
        MenuScreens.register(MIPipes.SCREEN_HANDLER_TYPE_ITEM_PIPE, ItemPipeScreen::new);
        MenuScreens.register(MIPipes.SCREEN_HANDLER_TYPE_FLUID_PIPE, FluidPipeScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(PipePackets.SET_PRIORITY, ClientPipePackets.ON_SET_PRIORITY);
        registerRenderers();

        WorldRenderEvents.BLOCK_OUTLINE.register((wrc, boc) -> {
            if (boc.blockState().getBlock() instanceof PipeBlock) {
                var shape = PipeBlock.getHitPart(wrc.world(), boc.blockPos(), (BlockHitResult) Minecraft.getInstance().hitResult);

                if (shape != null) {
                    BlockPos pos = boc.blockPos();
                    Vec3 camPos = wrc.camera().getPosition();
                    RenderHelper.renderVoxelShape(wrc.matrixStack(), wrc.consumers().getBuffer(RenderType.lines()), shape.shape,
                            pos.getX() - camPos.x(),
                            pos.getY() - camPos.y(),
                            pos.getZ() - camPos.z(),
                            0, 0, 0, 0.4f);
                    return false;
                }
            }

            return true;
        });

        ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
            if (result instanceof BlockHitResult bhr) {
                var targetedPart = PipeBlock.getHitPart(player.level, bhr.getBlockPos(), bhr);
                return new ItemStack(targetedPart == null ? Items.AIR : MIPipes.INSTANCE.getPipeItem(targetedPart.type));
            }

            return ItemStack.EMPTY;
        });

        PipeModelProvider.modelNames.addAll(MIPipes.PIPE_MODEL_NAMES);
    }

    private static PipeRenderer.Factory makeRenderer(List<String> sprites, boolean innerQuads) {
        return new PipeRenderer.Factory() {
            @Override
            public Collection<Material> getSpriteDependencies() {
                return sprites.stream().map(
                        n -> new net.minecraft.client.resources.model.Material(InventoryMenu.BLOCK_ATLAS, new MIIdentifier("block/pipes/" + n)))
                        .collect(Collectors.toList());
            }

            @Override
            public PipeRenderer create(Function<Material, TextureAtlasSprite> textureGetter) {
                net.minecraft.client.resources.model.Material[] ids = sprites.stream()
                        .map(n -> new net.minecraft.client.resources.model.Material(InventoryMenu.BLOCK_ATLAS,
                                new MIIdentifier("block/pipes/" + n)))
                        .toArray(net.minecraft.client.resources.model.Material[]::new);
                return new PipeMeshCache(textureGetter, ids, innerQuads);
            }
        };
    }

    private static final PipeRenderer.Factory ITEM_RENDERER = makeRenderer(Arrays.asList("item", "item_item", "item_in", "item_in_out", "item_out"),
            false);
    private static final PipeRenderer.Factory FLUID_RENDERER = makeRenderer(
            Arrays.asList("fluid", "fluid_item", "fluid_in", "fluid_in_out", "fluid_out"), true);
    private static final PipeRenderer.Factory ELECTRICITY_RENDERER = makeRenderer(Arrays.asList("electricity", "electricity_blocks"), false);

    public static final List<PipeRenderer.Factory> RENDERERS = List.of(ITEM_RENDERER, FLUID_RENDERER, ELECTRICITY_RENDERER);

    private void registerRenderers() {
        for (var type : PipeNetworkType.getTypes().values()) {
            if (type.getIdentifier().getPath().endsWith("item_pipe")) {
                PipeRenderer.register(type, ITEM_RENDERER);
            } else if (type.getIdentifier().getPath().endsWith("fluid_pipe")) {
                PipeRenderer.register(type, FLUID_RENDERER);
            } else if (type.getIdentifier().getPath().endsWith("cable")) {
                PipeRenderer.register(type, ELECTRICITY_RENDERER);
            }
        }
    }
}