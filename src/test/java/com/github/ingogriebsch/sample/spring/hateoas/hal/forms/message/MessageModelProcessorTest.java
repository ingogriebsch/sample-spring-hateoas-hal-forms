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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

public class MessageModelProcessorTest {

    @Nested
    class Process {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(NullPointerException.class, () -> new MessageModelProcessor().process(null));
        }

        @Test
        public void should_return_enhanced_input() {
            MessageModel messageModel = new MessageModel(1L, 2L, "title", "content");
            assertThat(new MessageModelProcessor().process(messageModel)).isSameAs(messageModel);
        }

        @Test
        public void should_return_model_containing_messages_link() {
            MessageModel messageModel = new MessageModelProcessor().process(new MessageModel(1L, 2L, "name", "description"));
            assertThat(messageModel).isNotNull();

            List<Link> links = messageModel.getLinks("parent");
            assertThat(links).isNotNull().hasSize(1);
            Link link = links.iterator().next();

            assertThat(link.getHref()).endsWith("/api/inboxes/" + messageModel.getInboxId());
        }

    }
}
