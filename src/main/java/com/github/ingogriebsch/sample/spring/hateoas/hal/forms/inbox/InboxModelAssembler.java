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

import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InboxModelAssembler implements RepresentationModelAssembler<InboxProjection, InboxModel> {

    @NonNull
    private final PagedResourcesAssembler<InboxProjection> pagedResourcesAssembler;
    @NonNull
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;

    @Override
    public InboxModel toModel(@NonNull InboxProjection source) {
        return instantiate(source).add(modelSelfLink(source.getId()));
    }

    public PagedModel<InboxModel> toPagedModel(@NonNull Page<InboxProjection> source) {
        return pagedResourcesAssembler.toModel(source, this, pagedModelSelfLink(source));
    }

    private Link pagedModelSelfLink(Page<InboxProjection> source) {
        Pageable pageable = source.getPageable();
        Link selfLink = linkTo(methodOn(InboxController.class).findAll(null)).withSelfRel();

        UriComponentsBuilder builder = fromUri(selfLink.getTemplate().expand());
        pageableResolver.enhance(builder, null, pageable);

        return new Link(of(builder.build().toString()), SELF).andAffordances(selfLink.getAffordances())
            .andAffordance(afford(methodOn(InboxController.class).insert(null)));
    }

    private static Link modelSelfLink(Long id) {
        Link self = linkTo(methodOn(InboxController.class).findOne(id)).withSelfRel();
        self = self.andAffordance(afford(methodOn(InboxController.class).update(id, null)));
        self = self.andAffordance(afford(methodOn(InboxController.class).delete(id)));
        return self;
    }

    private static InboxModel instantiate(InboxProjection source) {
        InboxModel target = new InboxModel();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        return target;
    }

}
