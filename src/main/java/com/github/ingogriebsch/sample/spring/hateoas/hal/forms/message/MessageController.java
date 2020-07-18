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

import static java.util.stream.Collectors.toList;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.List;

import javax.validation.Valid;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MessageController {

    static final String PATH_FIND_ALL = "/api/inboxes/{inboxId}/messages";
    static final String PATH_FIND_ONE = "/api/inboxes/{inboxId}/messages/{id}";
    static final String PATH_INSERT = PATH_FIND_ALL;
    static final String PATH_UPDATE = PATH_FIND_ONE;
    static final String PATH_DELETE = PATH_FIND_ONE;

    @NonNull
    private final MessageService messageService;
    @NonNull
    private final MessageModelAssembler messageModelAssembler;

    @GetMapping(path = PATH_FIND_ALL)
    public ResponseEntity<PagedModel<MessageModel>> findAll(@PathVariable Long inboxId, Pageable pageable) {
        return ok(messageModelAssembler.toPagedModel(inboxId, convert(inboxId, messageService.findAll(inboxId, pageable))));
    }

    @GetMapping(path = PATH_FIND_ONE)
    public ResponseEntity<MessageModel> findOne(@PathVariable Long inboxId, @PathVariable Long id) {
        return messageService.findOne(inboxId, id).map(m -> ok(messageModelAssembler.toModel(convert(inboxId, m))))
            .orElse(notFound().build());
    }

    @PostMapping(path = PATH_INSERT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageModel> insert(@PathVariable Long inboxId, @RequestBody @Valid MessageInput messageInput) {
        return status(CREATED)
            .body(messageModelAssembler.toModel(convert(inboxId, messageService.insert(inboxId, messageInput))));
    }

    @PutMapping(path = PATH_UPDATE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageModel> update(@PathVariable Long inboxId, @PathVariable Long id,
        @RequestBody @Valid MessageInput messageInput) {
        return messageService.update(inboxId, id, messageInput).map(m -> ok(messageModelAssembler.toModel(convert(inboxId, m))))
            .orElse(notFound().build());
    }

    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long inboxId, @PathVariable Long id) {
        return messageService.delete(inboxId, id) ? ok().build() : notFound().build();
    }

    private static Page<MessageProjection> convert(Long inboxId, Page<Message> messages) {
        return new PageImpl<>(convert(inboxId, messages.getContent()), messages.getPageable(), messages.getTotalElements());
    }

    private static List<MessageProjection> convert(Long inboxId, List<Message> messages) {
        return messages.stream().map(i -> convert(inboxId, i)).collect(toList());
    }

    private static MessageProjection convert(Long inboxId, Message message) {
        return new MessageProjection(inboxId, message.getId(), message.getTitle(), message.getContent());
    }
}
