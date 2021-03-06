/*
 * Copyright 2012-2014 Matt Baxter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kitteh.tag.handler;

import org.kitteh.tag.api.IPacketHandler;
import org.kitteh.tag.api.TagHandler;
import org.kitteh.tag.api.TagInfo;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

public class ProtocolLibHandler implements IPacketHandler {

    private final TagHandler handler;

    public ProtocolLibHandler(TagHandler handler) {
        this.handler = handler;
    }

    @Override
    public void shutdown() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this.handler.getPlugin());
    }

    @Override
    public void startup() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.handler.getPlugin(), ConnectionSide.SERVER_SIDE, ListenerPriority.HIGH, Packets.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketID() != Packets.Server.NAMED_ENTITY_SPAWN) {
                    return;
                }
                final PacketContainer packetContainer = event.getPacket();
                try {
                    final TagInfo info = ProtocolLibHandler.this.handler.getNameForPacket20(null, packetContainer.getSpecificModifier(int.class).read(0), packetContainer.getSpecificModifier(String.class).read(0), event.getPlayer());
                    if (info != null) {
                        packetContainer.getSpecificModifier(String.class).write(0, info.getName());
                    }
                } catch (final FieldAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}