/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.nms.v1_8_R1;

import com.jcwhatever.nucleus.nms.INmsTitleHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.reflection.Fields;
import com.jcwhatever.nucleus.utils.reflection.ReflectedInstance;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.EnumTitleAction;

import javax.annotation.Nullable;

/**
 * Minecraft Title packet sender for NMS version v1_8_R1
 */
public final class NmsTitleHandler_v1_8_R1 extends v1_8_R1 implements INmsTitleHandler {

    /**
     * Send the packet to a player.
     *
     * @param player        The player to send the title to.
     * @param jsonTitle     The Json title text.
     * @param jsonSubtitle  Optional Json subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    @Override
    public void send(Player player, String jsonTitle, @Nullable String jsonSubtitle,
                     int fadeIn, int stay, int fadeOut) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(jsonTitle);

        Object titleComponent = _ChatSerializer.invoke(null, "serialize", jsonTitle);

        Fields playerFields = _EntityPlayer.reflect(
                _CraftPlayer.reflect(player).invoke("getHandle")).getFields();

        ReflectedInstance connection = _PlayerConnection.reflect(playerFields.get("playerConnection"));

        Object timesPacket = _PacketPlayOutTitle.newInstance(fadeIn, stay, fadeOut);
        connection.invoke("sendPacket", timesPacket);

        if (jsonSubtitle != null) {
            Object subTitleComponent = _ChatSerializer.invoke(null, "serialize", jsonSubtitle);
            //TODO: add enum reflection
            Object subTitlePacket = _PacketPlayOutTitle.newInstance(EnumTitleAction.SUBTITLE, subTitleComponent);
            connection.invoke("sendPacket", subTitlePacket);
        }

        Object titlePacket = _PacketPlayOutTitle.newInstance(EnumTitleAction.TITLE, titleComponent);
        connection.invoke("sendPacket", titlePacket);
    }
}
