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
package com.github.ingogriebsch.sample.spring.hateoas.hal.forms;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.util.ArrayList;

import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.Inbox;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxInput;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxService;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.Message;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageInput;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Startup implements CommandLineRunner {

    // @formatter:off
    private static final ArrayList<MessageInput> MESSAGES = newArrayList(
        new MessageInput("Message 1", "M1 " + randomAlphabetic(12)),
        new MessageInput("Message 2", "M2 " + randomAlphabetic(12)), 
        new MessageInput("Message 3", "M3 " + randomAlphabetic(12)),
        new MessageInput("Message 4", "M4 " + randomAlphabetic(12)), 
        new MessageInput("Message 5", "M5 " + randomAlphabetic(12)));
    // @formatter:on

    // @formatter:off
    private static final ArrayList<InboxInput> INBOXES = newArrayList(
        new InboxInput("Inbox 1", "I1 "  + randomAlphabetic(12)),
        new InboxInput("Inbox 2", "I2 " + randomAlphabetic(12)), 
        new InboxInput("Inbox 3", "I3 " + randomAlphabetic(12)));
    // @formatter:on

    @NonNull
    private final InboxService inboxService;
    @NonNull
    private final MessageService messageService;

    @Override
    public void run(String... args) throws Exception {
        INBOXES.stream().forEach(i -> insert(i));
    }

    private void insert(InboxInput inboxInput) {
        Inbox inbox = inboxService.insert(inboxInput);
        log.debug("Inserted inbox '{}'.", inbox);
        insertMessages(inbox.getId());
    }

    private void insertMessages(Long inboxId) {
        MESSAGES.stream().forEach(m -> insert(inboxId, m));
    }

    private void insert(Long inboxId, MessageInput messageInput) {
        Message message = messageService.insert(inboxId, messageInput);
        log.debug("Inserted message '{}'.", message);
    }
}
