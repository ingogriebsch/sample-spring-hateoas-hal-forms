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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

public class InboxModelAssemblerTest {

    @Nested
    class ToModel {

        @Test
        public void should_throw_exception_if_input_is_null() {
            InboxModelAssembler assembler = inboxModelAssembler();
            assertThrows(NullPointerException.class, () -> assembler.toModel(null));
        }

        @Test
        public void should_return_model_on_legal_input() {
            InboxModelAssembler assembler = inboxModelAssembler();
            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            assertThat(assembler.toModel(inbox)).isNotNull();
        }

        @Test
        public void should_return_model_containing_self_link() {
            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            InboxModelAssembler assembler = inboxModelAssembler();

            InboxModel inboxModel = assembler.toModel(inbox);
            List<Link> links = inboxModel.getLinks(SELF);
            assertThat(links).isNotNull().hasSize(1);
            Link link = links.iterator().next();
            assertThat(link.getHref()).endsWith("/api/inboxes/" + inbox.getId());
        }

        @Test
        public void should_return_model_having_affordances_attached_to_self_link() {
            InboxModelAssembler assembler = inboxModelAssembler();
            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            assertThat(assembler.toModel(inbox)).isNotNull();

            InboxModel inboxModel = assembler.toModel(inbox);
            List<Link> links = inboxModel.getLinks(SELF);
            Link link = links.iterator().next();

            List<Affordance> affordances = link.getAffordances();
            assertThat(affordances).hasSize(3);

            AffordanceModel affordanceModel = affordances.get(0).getAffordanceModel(HAL_FORMS_JSON);
            assertThat(affordanceModel.getName()).isEqualTo("findOne");

            affordanceModel = affordances.get(1).getAffordanceModel(HAL_FORMS_JSON);
            assertThat(affordanceModel.getName()).isEqualTo("update");

            affordanceModel = affordances.get(2).getAffordanceModel(HAL_FORMS_JSON);
            assertThat(affordanceModel.getName()).isEqualTo("delete");
        }
    }

    @Nested
    class ToPagedModel {

        @Test
        public void should_throw_exception_if_input_is_null() {
            InboxModelAssembler assembler = inboxModelAssembler();
            assertThrows(NullPointerException.class, () -> assembler.toPagedModel(null));
        }

        @Test
        public void should_return_model_on_legal_input() {
            InboxModelAssembler assembler = inboxModelAssembler();
            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            assertThat(assembler.toPagedModel(new PageImpl<>(newArrayList(inbox)))).isNotNull();
        }

        @Test
        public void should_return_model_containing_self_link() {
            InboxModelAssembler assembler = inboxModelAssembler();
            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            Page<InboxProjection> page = new PageImpl<>(newArrayList(inbox), of(0, 5), 1);

            PagedModel<InboxModel> pagedModel = assembler.toPagedModel(page);

            List<Link> links = pagedModel.getLinks(SELF);
            assertThat(links).isNotNull().hasSize(1);
            Link link = links.iterator().next();
            assertThat(link.getRel()).isEqualTo(SELF);
            assertThat(link.getHref()).endsWith("/api/inboxes?page=0&size=5");
        }

        @Test
        public void should_return_model_having_affordances_attached_to_self_link() {
            InboxModelAssembler assembler = inboxModelAssembler();

            InboxProjection inbox = new InboxProjection(1L, "name", "description");
            Page<InboxProjection> page = new PageImpl<>(newArrayList(inbox), of(0, 5), 1);

            PagedModel<InboxModel> pagedModel = assembler.toPagedModel(page);

            List<Link> links = pagedModel.getLinks(SELF);
            Link link = links.iterator().next();

            List<Affordance> affordances = link.getAffordances();
            assertThat(affordances).hasSize(2);

            AffordanceModel affordanceModel = affordances.get(0).getAffordanceModel(HAL_FORMS_JSON);
            assertThat(affordanceModel.getName()).isEqualTo("findAll");

            affordanceModel = affordances.get(1).getAffordanceModel(HAL_FORMS_JSON);
            assertThat(affordanceModel.getName()).isEqualTo("insert");
        }
    }

    private static InboxModelAssembler inboxModelAssembler() {
        return new InboxModelAssembler(new PagedResourcesAssembler<InboxProjection>(null, null),
            new HateoasPageableHandlerMethodArgumentResolver());
    }
}
