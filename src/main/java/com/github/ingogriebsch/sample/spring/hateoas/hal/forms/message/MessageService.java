/*-
 * #%L
 * Spring HATEOAS HAL-FORMS sample
 * %%
 * Copyright (C) 2018 - 2019 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.PageUtils.toPage;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.MoreCollectors.toOptional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class MessageService {

    private final Map<Long, List<Message>> messagesByInbox = newHashMap();

    public Page<Message> findAll(@NonNull Long inboxId, @NonNull Pageable pageable) {
        return toPage(messagesByInbox.containsKey(inboxId) ? newArrayList(messagesByInbox.get(inboxId)) : newArrayList(),
            pageable);
    }

    public Optional<Message> findOne(@NonNull Long inboxId, @NonNull Long id) {
        List<Message> messages = messagesByInbox.get(inboxId);
        if (messages == null) {
            return empty();
        }
        return messages.stream().filter(p -> p.getId().equals(id)).limit(1).findAny();
    }

    public Message insert(@NonNull Long inboxId, @NonNull MessageInput messageInput) {
        List<Message> messages = messagesByInbox.get(inboxId);
        if (messages == null) {
            messages = newArrayList();
            messagesByInbox.put(inboxId, messages);
        }

        Message message = new Message();
        message.setId(nextId(messages));
        message = merge(message, messageInput);

        messages.add(message);
        return message;
    }

    public Optional<Message> update(@NonNull Long inboxId, @NonNull Long id, @NonNull MessageInput messageInput) {
        List<Message> messages = messagesByInbox.get(inboxId);
        if (messages == null) {
            return empty();
        }

        Optional<Message> message = messages.stream().filter(p -> id.equals(p.getId())).collect(toOptional());
        if (!message.isPresent()) {
            return empty();
        }

        return of(merge(message.get(), messageInput));
    }

    public boolean delete(@NonNull Long inboxId, @NonNull Long id) {
        List<Message> messages = messagesByInbox.get(inboxId);
        if (messages == null) {
            return false;
        }
        return messages.removeIf(p -> p.getId().equals(id));
    }

    private static Message merge(Message message, @NonNull MessageInput messageInput) {
        message.setTitle(messageInput.getTitle());
        message.setContent(messageInput.getContent());
        return message;
    }

    private static Long nextId(Collection<Message> messages) {
        Long id = 0L;
        for (Message message : messages) {
            Long messageId = message.getId();
            if (id < messageId) {
                id = messageId;
            }
        }
        return id + 1L;
    }

}
