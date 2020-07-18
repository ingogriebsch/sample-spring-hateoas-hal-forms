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
package com.github.ingogriebsch.sample.spring.hateoas.hal.forms.root;

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.root.RootController.PATH_ROOT;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.HateoasConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@Import({ HateoasConfiguration.class })
@WebMvcTest(RootController.class)
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Root {

        @Test
        public void should_return_ok_including_self_link() throws Exception {
            ResultActions actions = mockMvc.perform(get(PATH_ROOT).accept(HAL_FORMS_JSON));

            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));
            actions.andExpect(jsonPath("$._links.self.href", endsWith(PATH_ROOT)));

        }

        @Test
        public void should_return_ok_including_self_inboxLink() throws Exception {
            ResultActions actions = mockMvc.perform(get(PATH_ROOT).accept(HAL_FORMS_JSON));

            actions.andExpect(status().isOk());
            actions.andExpect(content().contentType(HAL_FORMS_JSON));
            actions.andExpect(jsonPath("$._links.inboxes.href", endsWith("/api/inboxes{?page,size,sort}")));
        }
    }
}
