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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.dynamic.IRenderable;

public interface IEntityRenderable<T extends Entity> extends IRenderable<IEntityRenderable.Context<T>>
{
    static <T extends Entity> IEntityRenderable<T> of(T entity)
    {
        return Minecraft.getInstance().getRenderManager().getRenderer(entity);
    }

    static <T extends Entity> IEntityRenderable<T> of(Class<T> teClass)
    {
        return Minecraft.getInstance().getRenderManager().getRenderer(teClass);
    }

    default EntityRenderer<T> getRenderer() {return (EntityRenderer<T>)this; }

    @Override
    default void render(Context<T> contextData)
    {
        getRenderer().doRender(contextData.getObject(), contextData.getX(), contextData.getY(), contextData.getZ(), contextData.getYaw(), contextData.getPartialTicks());
    }

    interface Context<T extends Entity>
    {
        T getObject();
        double getX();
        double getY();
        double getZ();
        float getYaw();
        float getPartialTicks();
    }
}
