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

package net.minecraftforge.client.model.dynamic;

import com.google.common.collect.Iterators;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class DynamicModelLoaderRegistry
{
    private static final NonNullLazy<DynamicModelLoaderRegistry> lazyInstance = NonNullLazy.concurrentOf(DynamicModelLoaderRegistry::new);
    public static DynamicModelLoaderRegistry instance() { return lazyInstance.get(); }

    private final List<IDynamicModelLoader> registry = new ArrayList<>();

    public DynamicModelLoaderRegistry()
    {
        Iterators.addAll(registry, ServiceLoader.load(IDynamicModelLoader.class).iterator());
    }

    public IRenderable<?> loadModel(ResourceLocation loc)
    {
        for(IDynamicModelLoader loader : registry)
        {
            if (loader.accepts(loc))
                return loader.load(loc);
        }
        throw new RuntimeException("Could not find a loader that accepts '" + loc + "'");
    }
}
