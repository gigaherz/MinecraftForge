/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model.dynamic.adapters;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.dynamic.IRenderable;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BakedModelRenderable implements IRenderable<BakedModelRenderable.Context>
{
    public static final Random RANDOM = new Random();

    public static BakedModelRenderable of(ItemStack stack)
    {
        return of(Minecraft.getInstance().getItemRenderer().getModelWithOverrides(stack));
    }

    public static BakedModelRenderable of(BlockState state)
    {
        return of(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state));
    }

    public static BakedModelRenderable of(IBakedModel model)
    {
        return new BakedModelRenderable(model);
    }

    private final IBakedModel model;

    protected BakedModelRenderable(IBakedModel model)
    {
        this.model = model;
    }

    @Override
    public void render(Context contextData)
    {
        IBakedModel actualModel = model;
        GlStateManager.pushMatrix();
        if (contextData.getPerspective() != null)
        {
            Pair<? extends IBakedModel, Matrix4f> pair = actualModel.handlePerspective(contextData.getPerspective());
            actualModel = pair.getLeft();
            Matrix4f matrix = pair.getRight();
            if (matrix != null)
                ForgeHooksClient.multiplyCurrentGlMatrix(matrix);
        }
        if (contextData.getStack() != null)
            actualModel = actualModel.getOverrides().getModelWithOverrides(actualModel, contextData.getStack(), contextData.getWorld(), contextData.getEntity());
        List<BakedQuad> quads = actualModel.getQuads(contextData.getBlockState(), contextData.getSide(), contextData.getRandom(), contextData.getModelData());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, quads.get(0).getFormat());
        for (BakedQuad bakedquad : quads)
        {
            int tint = contextData.getTintColor(bakedquad.getTintIndex());
            LightUtil.renderQuadColor(bufferBuilder, bakedquad, tint);
        }
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static class Context
    {
        private Function<Integer,Integer> tintGetter = x -> -1;
        private IModelData modelData = EmptyModelData.INSTANCE;
        @Nullable
        private BlockState blockState = null;
        @Nullable
        private Direction side = null;
        @Nullable
        private ItemStack stack = null;
        @Nullable
        private World world = null;
        @Nullable
        private ItemCameraTransforms.TransformType transformType = null;
        @Nullable
        private LivingEntity entity = null;
        private Random random = RANDOM;

        public Context withTintGetter(Function<Integer,Integer> tintGetter)
        {
            this.tintGetter = tintGetter;
            return this;
        }

        public Context withModelData(IModelData modelData)
        {
            this.modelData = modelData;
            return this;
        }

        public Context withSide(Direction side)
        {
            this.side = side;
            return this;
        }

        public Context withBlockState(@Nullable BlockState blockState)
        {
            this.blockState = blockState;
            return this;
        }

        public Context withRandom(Random random)
        {
            this.random = random;
            return this;
        }

        public Context withStack(ItemStack stack)
        {
            this.stack = stack;
            return this;
        }

        public Context withWorld(@Nullable World world)
        {
            this.world = world;
            return this;
        }

        public Context withEntity(@Nullable LivingEntity entity)
        {
            this.entity = entity;
            return this;
        }

        public Context withPerspective(@Nullable ItemCameraTransforms.TransformType transformType)
        {
            this.transformType = transformType;
            return this;
        }

        public int getTintColor(int tintIndex)
        {
            return tintGetter.apply(tintIndex);
        }

        public IModelData getModelData()
        {
            return modelData;
        }

        @Nullable
        public Direction getSide()
        {
            return side;
        }

        @Nullable
        public BlockState getBlockState()
        {
            return blockState;
        }

        public Random getRandom()
        {
            return random;
        }

        @Nullable
        public ItemStack getStack()
        {
            return stack;
        }

        @Nullable
        public World getWorld()
        {
            return world;
        }

        @Nullable
        public LivingEntity getEntity()
        {
            return entity;
        }

        @Nullable
        public ItemCameraTransforms.TransformType getPerspective()
        {
            return transformType;
        }
    }
}
