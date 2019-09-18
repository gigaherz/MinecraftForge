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

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.dynamic.IRenderable;

import java.util.function.BiConsumer;

public class BakedGeometryRenderable<T> implements IRenderable<T>
{
    private static final int BUFFER_SIZE = 2*1024*1024;
    private static final BufferBuilder BUILDER = new BufferBuilder(BUFFER_SIZE);

    public static <T> BakedGeometryRenderable<T> of(int glMode, VertexFormat fmt, BiConsumer<BufferBuilder, VertexFormat> vertexProducer)
    {
        VertexBuffer vbo = new VertexBuffer(fmt);
        BUILDER.begin(glMode, fmt);
        vertexProducer.accept(BUILDER, fmt);
        BUILDER.reset();
        vbo.bufferData(BUILDER.getByteBuffer());
        return new BakedGeometryRenderable<>(vbo, glMode);
    }

    final VertexBuffer vbo;
    final int glMode;

    public BakedGeometryRenderable(VertexBuffer vbo, int glMode)
    {
        this.vbo = vbo;
        this.glMode = glMode;
    }

    @Override
    public void render(T contextData)
    {
        vbo.drawArrays(glMode);
    }

    public void delete()
    {
        vbo.deleteGlBuffers();
    }
}
