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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.PageUtils.toPage;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.MoreCollectors.toOptional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InboxService {

    private final List<Inbox> inboxes = newArrayList();

    public Page<Inbox> findAll(@NonNull Pageable pageable) {
        return toPage(newArrayList(inboxes), pageable);
    }

    public Optional<Inbox> findOne(@NonNull Long id) {
        return inboxes.stream().filter(p -> p.getId().equals(id)).limit(1).findAny();
    }

    public Inbox insert(@NonNull InboxInput inboxInput) {
        Inbox inbox = new Inbox();
        inbox.setId(nextId(inboxes));
        inbox = merge(inbox, inboxInput);
        inboxes.add(inbox);
        return inbox;
    }

    public Optional<Inbox> update(@NonNull Long id, @NonNull InboxInput inboxInput) {
        Optional<Inbox> inbox = inboxes.stream().filter(p -> id.equals(p.getId())).collect(toOptional());
        if (!inbox.isPresent()) {
            return empty();
        }

        return of(merge(inbox.get(), inboxInput));
    }

    public boolean delete(@NonNull Long id) {
        return inboxes.removeIf(p -> p.getId().equals(id));
    }

    private static Inbox merge(Inbox inbox, @NonNull InboxInput inboxInput) {
        inbox.setName(inboxInput.getName());
        inbox.setDescription(inboxInput.getDescription());
        return inbox;
    }

    private static Long nextId(Collection<Inbox> inboxes) {
        Long id = 0L;
        for (Inbox inbox : inboxes) {
            Long inboxId = inbox.getId();
            if (id < inboxId) {
                id = inboxId;
            }
        }
        return id + 1L;
    }

}
