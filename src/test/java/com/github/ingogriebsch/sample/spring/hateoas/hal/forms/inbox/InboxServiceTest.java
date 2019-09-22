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
package com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox;

import static java.util.stream.Collectors.toList;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
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

public class InboxServiceTest {

    @Nested
    class FindAll {

        @Test
        public void should_return_matching_page_if_inside_range() throws Exception {
            InboxService inboxService = new InboxService();
            List<InboxInput> inboxInputs = newArrayList(inboxInput(), inboxInput(), inboxInput(), inboxInput(), inboxInput());
            inboxInputs.stream().forEach(p -> inboxService.insert(p));

            PageRequest pageable = of(0, 2);
            Page<Inbox> page = inboxService.findAll(pageable);
            assertThat(page).isNotNull();

            assertThat(page.getContent()).extracting("name", "description")
                .containsExactlyElementsOf(inboxInputs.subList((int) pageable.getOffset(), pageable.getPageSize()).stream()
                    .map(pi -> new Tuple(pi.getName(), pi.getDescription())).collect(toList()));
        }
    }

    @Nested
    class FindOne {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new InboxService().findOne(null));
        }

        @Test
        public void should_return_matching_inbox_if_available() throws Exception {
            InboxService inboxService = new InboxService();
            List<InboxInput> inboxInputs = newArrayList(inboxInput(), inboxInput(), inboxInput());
            List<Inbox> inboxes = inboxInputs.stream().map(p -> inboxService.insert(p)).collect(toList());

            Inbox inbox = inboxes.get(nextInt(0, inboxes.size()));
            Optional<Inbox> optional = inboxService.findOne(inbox.getId());

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualTo(inbox);
        }

        @Test
        public void should_return_empty_optional_if_not_available() throws Exception {
            InboxService inboxService = new InboxService();
            Optional<Inbox> optional = inboxService.findOne(nextLong());
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    @Nested
    class Insert {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new InboxService().insert(null));
        }

        @Test
        public void should_return_inbox_instance_if_input_is_legal() throws Exception {
            InboxService inboxService = new InboxService();
            InboxInput inboxInput = inboxInput();

            Inbox inbox = inboxService.insert(inboxInput);
            assertThat(inbox).isEqualToComparingOnlyGivenFields(inboxInput, "name", "description");
        }

    }

    @Nested
    class Update {

        @Test
        public void should_throw_exception_if_called_with_null_id() throws Exception {
            assertThrows(NullPointerException.class, () -> new InboxService().update(null, inboxInput()));
        }

        @Test
        public void should_throw_exception_if_called_with_null_input() throws Exception {
            assertThrows(NullPointerException.class, () -> new InboxService().update(1L, null));
        }

        @Test
        public void should_return_optional_containing_updated_inbox_instance_if_inbox_is_known() throws Exception {
            InboxService inboxService = new InboxService();
            InboxInput inboxInput = inboxInput();
            Inbox inbox = inboxService.insert(inboxInput);

            inboxInput = new InboxInput(inboxInput.getName(), inboxInput.getDescription() + "234");
            Optional<Inbox> optional = inboxService.update(inbox.getId(), inboxInput);

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isTrue();
            assertThat(optional.get()).isEqualToComparingOnlyGivenFields(inboxInput, "name", "description");
        }

        @Test
        public void should_return_empty_optional_if_inbox_is_not_known() throws Exception {
            InboxService inboxService = new InboxService();

            Optional<Inbox> optional = inboxService.update(nextLong(), inboxInput());

            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }
    }

    @Nested
    class Delete {

        @Test
        public void should_throw_exception_if_called_with_null() throws Exception {
            assertThrows(NullPointerException.class, () -> new InboxService().delete(null));
        }

        @Test
        public void should_return_false_if_inbox_is_not_known() throws Exception {
            InboxService inboxService = new InboxService();
            assertThat(inboxService.delete(nextLong())).isFalse();
        }

        @Test
        public void should_return_true_if_inbox_is_known() throws Exception {
            InboxService inboxService = new InboxService();
            List<InboxInput> inboxInputs = newArrayList(inboxInput(), inboxInput(), inboxInput());
            List<Inbox> inboxes = inboxInputs.stream().map(p -> inboxService.insert(p)).collect(toList());

            Inbox inbox = inboxes.get(nextInt(0, inboxes.size()));
            assertThat(inboxService.delete(inbox.getId())).isTrue();

            Optional<Inbox> optional = inboxService.findOne(inbox.getId());
            assertThat(optional).isNotNull();
            assertThat(optional.isPresent()).isFalse();
        }

    }

    private static InboxInput inboxInput() {
        return new InboxInput("name", "description");
    }

}
