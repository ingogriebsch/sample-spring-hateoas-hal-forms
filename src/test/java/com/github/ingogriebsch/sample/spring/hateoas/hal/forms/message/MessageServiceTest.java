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

import static java.util.stream.Collectors.toList;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;

import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class MessageServiceTest {

    @Nested
    class FindAll {

        @Test
        public void should_return_matching_page_if_inside_range() throws Exception {
            Long inboxId = 1L;
            MessageService messageService = new MessageService();
            List<MessageInput> messageInputs =
                newArrayList(messageInput(), messageInput(), messageInput(), messageInput(), messageInput());
            messageInputs.stream().forEach(m -> messageService.insert(inboxId, m));

            PageRequest pageable = of(0, 2);
            Page<Message> page = messageService.findAll(inboxId, pageable);
            assertThat(page).isNotNull();

            assertThat(page.getContent()).extracting("title", "content")
                .containsExactlyElementsOf(messageInputs.subList((int) pageable.getOffset(), pageable.getPageSize()).stream()
                    .map(pi -> new Tuple(pi.getTitle(), pi.getContent())).collect(toList()));
        }
    }

    @Nested
    class FindOne {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new MessageService().findOne(null, null));
        }

        @Test
        public void should_return_matching_message_if_available() throws Exception {
            Long inboxId = 1L;
            MessageService messageService = new MessageService();
            List<MessageInput> messageInputs = newArrayList(messageInput(), messageInput(), messageInput());
            List<Message> messages = messageInputs.stream().map(m -> messageService.insert(inboxId, m)).collect(toList());

            Message message = messages.get(nextInt(0, messages.size()));
            Optional<Message> optional = messageService.findOne(inboxId, message.getId());

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualTo(message);
        }

        @Test
        public void should_return_empty_optional_if_not_available() throws Exception {
            MessageService messageService = new MessageService();
            Optional<Message> optional = messageService.findOne(1L, 1L);
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    @Nested
    class Insert {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new MessageService().insert(null, null));
        }

        @Test
        public void should_return_message_instance_if_input_is_legal() throws Exception {
            Long inboxId = 1L;
            MessageService messageService = new MessageService();
            MessageInput messageInput = messageInput();

            Message message = messageService.insert(inboxId, messageInput);
            assertThat(message).isEqualToComparingOnlyGivenFields(messageInput, "title", "content");
        }

    }

    @Nested
    class Update {

        @Test
        public void should_throw_exception_if_called_with_null_id() throws Exception {
            assertThrows(NullPointerException.class, () -> new MessageService().update(1L, null, messageInput()));
        }

        @Test
        public void should_throw_exception_if_called_with_null_input() throws Exception {
            assertThrows(NullPointerException.class, () -> new MessageService().update(1L, 2L, null));
        }

        @Test
        public void should_return_optional_containing_updated_message_instance_if_message_is_known() throws Exception {
            Long inboxId = 1L;
            MessageService messageService = new MessageService();
            MessageInput messageInput = messageInput();
            Message message = messageService.insert(inboxId, messageInput);

            messageInput = new MessageInput(messageInput.getTitle(), messageInput.getContent() + "234");
            Optional<Message> optional = messageService.update(inboxId, message.getId(), messageInput);

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualToComparingOnlyGivenFields(messageInput, "title", "content");
        }

        @Test
        public void should_return_empty_optional_if_message_is_not_known() throws Exception {
            MessageService messageService = new MessageService();

            Optional<Message> optional = messageService.update(1L, 2L, messageInput());

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }
    }

    @Nested
    class Delete {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new MessageService().delete(null, null));
        }

        @Test
        public void should_return_false_if_message_is_not_known() throws Exception {
            MessageService messageService = new MessageService();
            assertThat(messageService.delete(1L, 1L)).isFalse();
        }

        @Test
        public void should_return_true_if_message_is_known() throws Exception {
            Long inboxId = 1L;
            MessageService messageService = new MessageService();
            List<MessageInput> messageInputs = newArrayList(messageInput(), messageInput(), messageInput());
            List<Message> messages = messageInputs.stream().map(m -> messageService.insert(inboxId, m)).collect(toList());

            Message message = messages.get(nextInt(0, messages.size()));
            assertThat(messageService.delete(inboxId, message.getId())).isTrue();

            Optional<Message> optional = messageService.findOne(inboxId, message.getId());
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    private static MessageInput messageInput() {
        return new MessageInput("name", "description");
    }

}
