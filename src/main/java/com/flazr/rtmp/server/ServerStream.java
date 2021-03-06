/*
 * Flazr <http://flazr.com> Copyright (C) 2009  Peter Thomas.
 *
 * This file is part of Flazr.
 *
 * Flazr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flazr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flazr.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.flazr.rtmp.server;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.message.Metadata;
import com.flazr.util.Utils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerStream {

    public Metadata getMeta() {
        return meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }

    public static enum PublishType {

        LIVE,
        APPEND,
        RECORD;

        public String asString() {
            return this.name().toLowerCase();
        }

        public static PublishType parse(final String raw) {
            return PublishType.valueOf(raw.toUpperCase());
        }

    }

    private final String name;
    private final PublishType publishType;
    private final ChannelGroup subscribers;
    private final List<RtmpMessage> configMessages;
    private Channel publisher;
    private Metadata meta;

    private static final Logger logger = LoggerFactory.getLogger(ServerStream.class);

    public ServerStream(final String rawName, final String typeString) {
        this.name = Utils.trimSlashes(rawName).toLowerCase();
        if (typeString != null) {
            this.publishType = PublishType.parse(typeString); // TODO record, append
            subscribers = new DefaultChannelGroup(name, new DefaultEventExecutor());
            configMessages = new ArrayList<RtmpMessage>();
        } else {
            this.publishType = null;
            subscribers = null;
            configMessages = null;
        }
        logger.info("Created ServerStream {}", this);
    }

    public boolean isLive() {
        return publishType != null && publishType == PublishType.LIVE;
    }

    public PublishType getPublishType() {
        return publishType;
    }

    public ChannelGroup getSubscribers() {
        return subscribers;
    }

    public String getName() {
        return name;
    }


    public List<RtmpMessage> getConfigMessages() {
        return configMessages;
    }

    public void addConfigMessage(final RtmpMessage message) {
        configMessages.add(message);
    }

    public void setPublisher(Channel publisher) {
        this.publisher = publisher;
        configMessages.clear();
    }

    public Channel getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[name: '").append(name);
        sb.append("' type: ").append(publishType);
        sb.append(" publisher: ").append(publisher);
        sb.append(" subscribers: ").append(subscribers);
        sb.append(" config: ").append(configMessages);
        sb.append(']');
        return sb.toString();
    }

}
