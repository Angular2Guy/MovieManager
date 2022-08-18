/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.domain.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.xxx.moviemanager.domain.model.entity.EntityBase;

public class CommonUtils {

	public static Date convert(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static <T extends EntityBase> Collection<Long> findDublicates(List<T> entities) {
		return entities.stream().collect(Collectors.toMap(EntityBase::getId, u -> false, (x, y) -> true)).entrySet()
				.stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	public static <T extends EntityBase> boolean filterForDublicates(T myEntity, Collection<Long> dublicates) {
		return dublicates.stream().filter(myId -> myId.equals(myEntity.getId())).findAny().isPresent();
	}

	public static <T extends EntityBase> Collection<T> filterDublicates(Collection<T> myCol) {
		return myCol.stream().collect(Collectors.toMap(EntityBase::getId, d -> d, (T x, T y) -> x == null ? y : x))
				.values();
	}
}
