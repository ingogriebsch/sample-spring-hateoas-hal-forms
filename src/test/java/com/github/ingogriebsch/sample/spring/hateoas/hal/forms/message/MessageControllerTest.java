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

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageController.PATH_DELETE;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageController.PATH_FIND_ALL;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageController.PATH_FIND_ONE;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageController.PATH_INSERT;
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
@Import({ HateoasConfiguration.class, MessageModelHateoasConfiguration.class })
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    @Nested
    class FindAll {

        @AfterEach
        public void afterEach() {
            reset(messageService);
        }

        @Test
        public void should_return_ok_including_resources_if_some_available() throws Exception {
            Long inboxId = 1L;
            Pageable pageable = of(0, 2);
            List<Message> messages = newArrayList(inbox(1L), inbox(2L), inbox(3L));
            Page<Message> page =
                new PageImpl<>(messages.subList((int) pageable.getOffset(), pageable.getPageSize()), pageable, messages.size());
            given(messageService.findAll(inboxId, pageable)).willReturn(page);

            ResultActions actions =
                mockMvc.perform(get(PATH_FIND_ALL, inboxId).params(pageableParams(pageable)).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(messageService, times(1)).findAll(inboxId, pageable);
            verifyNoMoreInteractions(messageService);
        }

        @Test
        public void should_return_ok_without_resources_if_none_available() throws Exception {
            Long inboxId = 1L;
            Pageable pageable = of(0, 2);
            given(messageService.findAll(inboxId, pageable)).willReturn(new PageImpl<>(newArrayList(), pageable, 0));

            ResultActions actions =
                mockMvc.perform(get(PATH_FIND_ALL, inboxId).params(pageableParams(pageable)).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(messageService, times(1)).findAll(inboxId, pageable);
            verifyNoMoreInteractions(messageService);
        }
    }

    @Nested
    class FindOne {

        @AfterEach
        public void afterEach() {
            reset(messageService);
        }

        @Test
        public void shoud_return_ok_and_inbox_resource_if_available() throws Exception {
            Long inboxId = 1L;
            Message inbox = inbox(1L);
            given(messageService.findOne(inboxId, inbox.getId())).willReturn(of(inbox));

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, inboxId, inbox.getId()).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(messageService, times(1)).findOne(inboxId, inbox.getId());
            verifyNoMoreInteractions(messageService);
        }

        @Test
        public void should_return_not_found_if_not_available() throws Exception {
            Long inboxId = 1L;
            Long id = 1L;
            given(messageService.findOne(inboxId, id)).willReturn(empty());

            ResultActions actions = mockMvc.perform(get(PATH_FIND_ONE, inboxId, id).accept(HAL_FORMS_JSON));
            actions.andExpect(status().isNotFound());

            verify(messageService, times(1)).findOne(inboxId, id);
            verifyNoMoreInteractions(messageService);
        }
    }

    @Nested
    class Insert {

        @AfterEach
        public void afterEach() {
            reset(messageService);
        }

        @Test
        public void should_return_created_if_input_is_legal() throws Exception {
            Long inboxId = 1L;
            MessageInput inboxInput = inboxInput();
            Message inbox = new Message(1L, inboxInput.getTitle(), inboxInput.getContent());
            given(messageService.insert(inboxId, inboxInput)).willReturn(inbox);

            ResultActions actions = mockMvc.perform(post(PATH_INSERT, inboxId).accept(HAL_FORMS_JSON)
                .contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(inboxInput)));

            actions.andExpect(status().isCreated());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));

            verify(messageService, times(1)).insert(inboxId, inboxInput);
            verifyNoMoreInteractions(messageService);
        }

        @Test
        public void should_return_bad_request_if_input_is_not_legal() throws Exception {
            Long inboxId = 1L;
            ResultActions actions = mockMvc.perform(post(PATH_INSERT, inboxId).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MessageInput())));

            actions.andExpect(status().isBadRequest());

            verifyZeroInteractions(messageService);
        }
    }

    @Nested
    class Update {

        @AfterEach
        public void afterEach() {
            reset(messageService);
        }

        // FIXME
    }

    @Nested
    class Delete {

        @AfterEach
        public void afterEach() {
            reset(messageService);
        }

        @Test
        public void should_return_ok_if_known() throws Exception {
            Long inboxId = 1L;
            Long id = nextLong();
            given(messageService.delete(inboxId, id)).willReturn(true);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, inboxId, id));
            actions.andExpect(status().isOk());

            verify(messageService, times(1)).delete(inboxId, id);
            verifyNoMoreInteractions(messageService);
        }

        @Test
        public void should_return_not_found_if_not_known() throws Exception {
            Long inboxId = 1L;
            Long id = nextLong();
            given(messageService.delete(inboxId, id)).willReturn(false);

            ResultActions actions = mockMvc.perform(delete(PATH_DELETE, inboxId, id));
            actions.andExpect(status().isNotFound());

            verify(messageService, times(1)).delete(inboxId, id);
            verifyNoMoreInteractions(messageService);
        }
    }

    private static Message inbox(Long id) {
        return new Message(id, "name", "description");
    }

    private static MessageInput inboxInput() {
        return new MessageInput("name", "description");
    }

    private static MultiValueMap<String, String> pageableParams(Pageable pageable) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "" + pageable.getPageNumber());
        params.add("size", "" + pageable.getPageSize());
        return params;
    }
}
