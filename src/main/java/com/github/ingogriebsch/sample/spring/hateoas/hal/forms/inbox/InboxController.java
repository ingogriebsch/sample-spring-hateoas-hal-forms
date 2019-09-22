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

import static java.util.stream.Collectors.toList;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.List;

import javax.validation.Valid;

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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class InboxController {

    static final String PATH_FIND_ALL = "/api/inboxes";
    static final String PATH_FIND_ONE = "/api/inboxes/{id}";
    static final String PATH_INSERT = PATH_FIND_ALL;
    static final String PATH_UPDATE = PATH_FIND_ONE;
    static final String PATH_DELETE = PATH_FIND_ONE;

    @NonNull
    private final InboxService inboxService;
    @NonNull
    private final InboxModelAssembler inboxModelAssembler;

    @GetMapping(path = PATH_FIND_ALL)
    public ResponseEntity<PagedModel<InboxModel>> findAll(Pageable pageable) {
        return ok(inboxModelAssembler.toPagedModel(convert(inboxService.findAll(pageable))));
    }

    @GetMapping(path = PATH_FIND_ONE)
    public ResponseEntity<InboxModel> findOne(@PathVariable Long id) {
        return inboxService.findOne(id).map(p -> ok(inboxModelAssembler.toModel(convert(p)))).orElse(notFound().build());
    }

    @PostMapping(path = PATH_INSERT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<InboxModel> insert(@RequestBody @Valid InboxInput inboxInput) {
        return status(CREATED).body(inboxModelAssembler.toModel(convert(inboxService.insert(inboxInput))));
    }

    @PutMapping(path = PATH_UPDATE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<InboxModel> update(@PathVariable Long id, @RequestBody @Valid InboxInput inboxInput) {
        return inboxService.update(id, inboxInput).map(p -> ok(inboxModelAssembler.toModel(convert(p))))
            .orElse(notFound().build());
    }

    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return inboxService.delete(id) ? ok().build() : notFound().build();
    }

    private static Page<InboxProjection> convert(Page<Inbox> inboxes) {
        return new PageImpl<>(convert(inboxes.getContent()), inboxes.getPageable(), inboxes.getTotalElements());
    }

    private static List<InboxProjection> convert(List<Inbox> content) {
        return content.stream().map(i -> convert(i)).collect(toList());
    }

    private static InboxProjection convert(Inbox inbox) {
        return new InboxProjection(inbox.getId(), inbox.getName(), inbox.getDescription());
    }
}
