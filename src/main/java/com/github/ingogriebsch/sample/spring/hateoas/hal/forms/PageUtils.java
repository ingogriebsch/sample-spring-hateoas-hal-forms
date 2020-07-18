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
package com.github.ingogriebsch.sample.spring.hateoas.hal.forms;

import static java.util.Collections.emptyList;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor(access = PRIVATE)
public final class PageUtils {

    public static <T> Page<T> toPage(@NonNull List<T> list, @NonNull Pageable pageable) {
        return new PageImpl<>(match(list, pageable), pageable, list.size());
    }

    static <T> List<T> match(@NonNull List<T> list, @NonNull Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();

        int firstElementIndex = getFirstElementIndex(list, offset);
        if (firstElementIndex >= list.size()) {
            return emptyList();
        }

        int lastElementIndex = getLastElementIndex(list, offset, pageable.getPageSize());
        return list.subList(firstElementIndex, lastElementIndex);
    }

    private static <T> int getFirstElementIndex(List<T> list, int offset) {
        return list.size() > offset ? offset : list.size();
    }

    private static <T> int getLastElementIndex(List<T> list, int offset, int pageSize) {
        int lastElementIndex = offset + pageSize;
        return list.size() > lastElementIndex ? lastElementIndex : list.size();
    }

}
