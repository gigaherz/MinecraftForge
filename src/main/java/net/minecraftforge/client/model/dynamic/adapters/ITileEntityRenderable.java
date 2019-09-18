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

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.dynamic.IRenderable;

public interface ITileEntityRenderable<T extends TileEntity> extends IRenderable<ITileEntityRenderable.Context<T>>
{
    static <T extends TileEntity> ITileEntityRenderable<T> of(T te)
    {
        return TileEntityRendererDispatcher.instance.getRenderer(te);
    }

    static <T extends TileEntity> ITileEntityRenderable<T> of(Class<T> teClass)
    {
        return TileEntityRendererDispatcher.instance.getRenderer(teClass);
    }

    default TileEntityRenderer<T> getRenderer() {return (TileEntityRenderer<T>)this; }

    @Override
    default void render(Context<T> contextData)
    {
        getRenderer().render(contextData.getObject(), contextData.getX(), contextData.getY(), contextData.getZ(), contextData.getPartialTicks(), contextData.getDestroyStage());
    }

    interface Context<T extends TileEntity>
    {
        T getObject();
        double getX();
        double getY();
        double getZ();
        float getPartialTicks();
        int getDestroyStage();
    }
}
