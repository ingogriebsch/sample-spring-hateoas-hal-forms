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

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import java.util.List;

import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
public class RootController {

    static final String REL_INBOXES = "inboxes";
    static final String PATH_ROOT = "/api";

    @NonNull
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;

    @GetMapping(PATH_ROOT)
    public ResponseEntity<RepresentationModel<?>> root() {
        return ok(new RepresentationModel<>(links()));
    }

    private List<Link> links() {
        return newArrayList(selfLink(), inboxesLink());
    }

    private Link inboxesLink() {
        Link inboxesLink = linkTo(methodOn(InboxController.class).findAll(null)).withRel(REL_INBOXES);

        UriComponentsBuilder builder = fromUri(inboxesLink.getTemplate().expand());
        TemplateVariables templateVariables = pageableResolver.getPaginationTemplateVariables(null, builder.build());

        UriTemplate template = of(inboxesLink.getHref()).with(templateVariables);
        return new Link(template.toString(), REL_INBOXES);
    }

    private static Link selfLink() {
        return linkTo(methodOn(RootController.class).root()).withSelfRel();
    }

}
