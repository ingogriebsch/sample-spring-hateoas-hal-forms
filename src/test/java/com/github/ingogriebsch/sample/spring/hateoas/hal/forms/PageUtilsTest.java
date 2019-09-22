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

import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.PageUtils.match;
import static com.github.ingogriebsch.sample.spring.hateoas.hal.forms.PageUtils.toPage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.domain.PageRequest.of;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class PageUtilsTest {

    @Nested
    class Match {

        @Test
        public void throws_exception_if_input_is_null() {
            assertThrows(NullPointerException.class, () -> match(null, null));
        }

        @Test
        public void returns_matching_list_if_input_is_empty() {
            List<Object> match = match(newArrayList(), of(0, 20));
            assertThat(match).isEmpty();
        }

        @Test
        public void returns_matching_list_if_input_is_bigger_as_page_request() {
            int size = 2;
            List<Object> match = match(newArrayList("a", "b", "c"), of(0, size));
            assertThat(match).hasSize(size);
        }

        @Test
        public void returns_matching_list_if_page_request_is_beyond_input() {
            int size = 2;
            List<Object> match = match(newArrayList("a", "b", "c"), of(2, size));
            assertThat(match).isEmpty();
        }

        @Test
        public void returns_matching_list_if_called_several_times_with_the_same_input() {
            Pageable pageable = of(0, 2);
            List<Object> input = newArrayList("a", "b", "c");

            List<Object> match = match(input, pageable);
            assertThat(match).hasSize(pageable.getPageSize()).containsExactly(input.get(0), input.get(1));

            match = match(match, pageable);
            assertThat(match).hasSize(pageable.getPageSize()).containsExactly(input.get(0), input.get(1));

            match = match(match, pageable);
            assertThat(match).hasSize(pageable.getPageSize()).containsExactly(input.get(0), input.get(1));
        }
    }

    @Nested
    class ToPage {

        @Test
        public void throws_exception_if_input_is_null() {
            assertThrows(NullPointerException.class, () -> toPage(null, null));
        }

    }

}
