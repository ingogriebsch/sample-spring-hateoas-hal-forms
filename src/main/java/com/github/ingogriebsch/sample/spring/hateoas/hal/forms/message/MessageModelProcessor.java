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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.github.ingogriebsch.sample.spring.hateoas.hal.forms.inbox.InboxController;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;

import lombok.NonNull;

public class MessageModelProcessor implements RepresentationModelProcessor<MessageModel> {

    static final String REL_PARENT = "parent";

    @Override
    public MessageModel process(@NonNull MessageModel model) {
        return model.add(parentLink(model.getInboxId()));
    }

    private static Link parentLink(Long inboxId) {
        return linkTo(methodOn(InboxController.class).findOne(inboxId)).withRel(REL_PARENT);
    }

}
