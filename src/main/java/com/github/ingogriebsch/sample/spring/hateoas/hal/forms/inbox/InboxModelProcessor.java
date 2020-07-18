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

import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.message.MessageController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class InboxModelProcessor implements RepresentationModelProcessor<InboxModel> {

    static final String REL_MESSAGES = "messages";

    @NonNull
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;

    @Override
    public InboxModel process(@NonNull InboxModel model) {
        return model.add(messagesLink(model.getId()));
    }

    private Link messagesLink(Long id) {
        Link messagesLink = linkTo(methodOn(MessageController.class).findAll(id, null)).withRel(REL_MESSAGES);

        UriComponentsBuilder builder = fromUri(messagesLink.getTemplate().expand());
        TemplateVariables templateVariables = pageableResolver.getPaginationTemplateVariables(null, builder.build());

        UriTemplate template = of(messagesLink.getHref()).with(templateVariables);
        return new Link(template.toString(), REL_MESSAGES);
    }

}
