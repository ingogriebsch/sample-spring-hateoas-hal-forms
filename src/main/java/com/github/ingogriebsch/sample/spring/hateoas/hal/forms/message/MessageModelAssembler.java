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

import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.UriTemplate.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class MessageModelAssembler implements RepresentationModelAssembler<MessageProjection, MessageModel> {

    @NonNull
    private final PagedResourcesAssembler<MessageProjection> pagedResourcesAssembler;
    @NonNull
    private final HateoasPageableHandlerMethodArgumentResolver pageableResolver;

    @Override
    public MessageModel toModel(@NonNull MessageProjection source) {
        return instantiate(source).add(modelSelfLink(source.getInboxId(), source.getId()));
    }

    public PagedModel<MessageModel> toPagedModel(@NonNull Long inboxId, @NonNull Page<MessageProjection> source) {
        return pagedResourcesAssembler.toModel(source, this, pagedModelSelfLink(inboxId, source));
    }

    private Link pagedModelSelfLink(Long inboxId, Page<MessageProjection> source) {
        Pageable pageable = source.getPageable();
        Link selfLink = linkTo(methodOn(MessageController.class).findAll(inboxId, null)).withSelfRel();

        UriComponentsBuilder builder = fromUri(selfLink.getTemplate().expand());
        pageableResolver.enhance(builder, null, pageable);

        return new Link(of(builder.build().toString()), SELF).andAffordances(selfLink.getAffordances())
            .andAffordance(afford(methodOn(MessageController.class).insert(inboxId, null)));
    }

    private static Link modelSelfLink(Long inboxId, Long id) {
        Link self = linkTo(methodOn(MessageController.class).findOne(inboxId, id)).withSelfRel();
        self = self.andAffordance(afford(methodOn(MessageController.class).update(inboxId, id, null)));
        self = self.andAffordance(afford(methodOn(MessageController.class).delete(inboxId, id)));
        return self;
    }

    private static MessageModel instantiate(MessageProjection source) {
        MessageModel target = new MessageModel();
        target.setInboxId(source.getInboxId());
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        return target;
    }

}
