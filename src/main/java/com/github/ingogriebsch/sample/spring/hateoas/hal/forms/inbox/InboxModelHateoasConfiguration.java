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

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;

@Configuration
public class InboxModelHateoasConfiguration {

    @Bean
    public InboxModelAssembler inboxModelAssembler(@NonNull PagedResourcesAssembler<InboxProjection> pagedResourcesAssembler,
        @NonNull HateoasPageableHandlerMethodArgumentResolver hateoasPageableHandlerMethodArgumentResolver) {
        return new InboxModelAssembler(pagedResourcesAssembler, hateoasPageableHandlerMethodArgumentResolver);
    }

    @Bean
    public InboxModelProcessor
        inboxModelProcessor(@NonNull HateoasPageableHandlerMethodArgumentResolver hateoasPageableHandlerMethodArgumentResolver) {
        return new InboxModelProcessor(hateoasPageableHandlerMethodArgumentResolver);
    }

}
