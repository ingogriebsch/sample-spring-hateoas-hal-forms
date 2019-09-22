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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;

public class InboxModelProcessorTest {

    @Nested
    class Process {

        @Test
        public void should_throw_exception_if_input_is_null() {
            assertThrows(NullPointerException.class, () -> inboxProcessor().process(null));
        }

        @Test
        public void should_return_enhanced_input() {
            InboxModel inboxModel = new InboxModel(1L, "name", "description");
            assertThat(inboxProcessor().process(inboxModel)).isSameAs(inboxModel);
        }

        @Test
        public void should_return_model_containing_messages_link() {
            InboxModel inboxModel = inboxProcessor().process(new InboxModel(1L, "name", "description"));
            assertThat(inboxModel).isNotNull();

            List<Link> links = inboxModel.getLinks("messages");
            assertThat(links).isNotNull().hasSize(1);
            Link link = links.iterator().next();

            assertThat(link.getHref()).endsWith("/api/inboxes/" + inboxModel.getId() + "/messages{?page,size,sort}");
        }
    }

    private static InboxModelProcessor inboxProcessor() {
        return new InboxModelProcessor(new HateoasPageableHandlerMethodArgumentResolver());
    }
}
