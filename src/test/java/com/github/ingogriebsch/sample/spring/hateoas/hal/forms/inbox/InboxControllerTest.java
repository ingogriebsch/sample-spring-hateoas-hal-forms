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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController.PATH_INSERT;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.HateoasConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
@Import({ HateoasConfiguration.class, InboxModelHateoasConfiguration.class })
@WebMvcTest(InboxController.class)
public class InboxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InboxService inboxService;

    @Nested
    class FindAll {

        @AfterEach
        public void afterEach() {
            reset(inboxService);
        }

        @Test
        public void should_return_ok_including_resources_if_some_available() throws Exception {
            Pageable pageable = of(0, 2);
            List<Inbox> inboxes = newArrayList(inbox(1L), inbox(2L), inbox(3L));
            Page<Inbox> page =
                new PageImpl<>(inboxes.subList((int) pageable.getOffset(), pageable.getPageSize()), pageable, inboxes.size());
            given(inboxService.findAll(pageable)).willReturn(page);

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).params(pageableParams(pageable)).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(inboxService, times(1)).findAll(pageable);
            verifyNoMoreInteractions(inboxService);
        }

        @Test
        public void should_return_ok_without_resources_if_none_available() throws Exception {
            Pageable pageable = of(0, 2);
            Page<Inbox> page = new PageImpl<>(newArrayList(), pageable, 0);
            given(inboxService.findAll(pageable)).willReturn(page);

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ALL).params(pageableParams(pageable)).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(inboxService, times(1)).findAll(pageable);
            verifyNoMoreInteractions(inboxService);
        }
    }

    @Nested
    class FindOne {

        @AfterEach
        public void afterEach() {
            reset(inboxService);
        }

        @Test
        public void shoud_return_ok_and_inbox_resource_if_available() throws Exception {
            Inbox inbox = inbox(1L);
            given(inboxService.findOne(inbox.getId())).willReturn(of(inbox));

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, inbox.getId()).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(inboxService, times(1)).findOne(inbox.getId());
            verifyNoMoreInteractions(inboxService);
        }

        @Test
        public void should_return_not_found_if_not_available() throws Exception {
            Long id = nextLong();
            given(inboxService.findOne(id)).willReturn(empty());

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, id).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isNotFound());

            verify(inboxService, times(1)).findOne(id);
            verifyNoMoreInteractions(inboxService);
        }
    }

    @Nested
    class Insert {

        @AfterEach
        public void afterEach() {
            reset(inboxService);
        }

        @Test
        public void should_return_created_if_input_is_legal() throws Exception {
            InboxInput inboxInput = inboxInput();
            Inbox inbox = new Inbox(1L, inboxInput.getName(), inboxInput.getDescription());
            given(inboxService.insert(inboxInput)).willReturn(inbox);

            ResultActions actions = mockMvc.perform(post(PATH_INSERT).accept(HAL_FORMS_JSON).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inboxInput)));

            actions.andExpect(status().isCreated());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(inboxService, times(1)).insert(inboxInput);
            verifyNoMoreInteractions(inboxService);
        }

        @Test
        public void should_return_bad_request_if_input_is_not_legal() throws Exception {
            ResultActions actions = mockMvc.perform(
                post(PATH_INSERT).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(new InboxInput())));

            actions.andExpect(status().isBadRequest());

            verifyZeroInteractions(inboxService);
        }
    }

    @Nested
    class Update {

        @AfterEach
        public void afterEach() {
            reset(inboxService);
        }

        // FIXME
    }

    @Nested
    class Delete {

        @AfterEach
        public void afterEach() {
            reset(inboxService);
        }

        @Test
        public void should_return_ok_if_known() throws Exception {
            Long id = nextLong();
            given(inboxService.delete(id)).willReturn(true);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, id));
            actions.andExpect(status().isOk());

            verify(inboxService, times(1)).delete(id);
            verifyNoMoreInteractions(inboxService);
        }

        @Test
        public void should_return_not_found_if_not_known() throws Exception {
            Long id = nextLong();
            given(inboxService.delete(id)).willReturn(false);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, id));
            actions.andExpect(status().isNotFound());

            verify(inboxService, times(1)).delete(id);
            verifyNoMoreInteractions(inboxService);
        }
    }

    private static Inbox inbox(Long id) {
        return new Inbox(id, "name", "description");
    }

    private static InboxInput inboxInput() {
        return new InboxInput("name", "description");
    }

    private static MultiValueMap<String, String> pageableParams(Pageable pageable) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "" + pageable.getPageNumber());
        params.add("size", "" + pageable.getPageSize());
        return params;
    }
}
