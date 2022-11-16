/**
 *    Copyright 2019 Sven Loesekann
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
package ch.xxx.moviemanager.domain.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class RevokedToken extends EntityBase {
	@NotBlank
	@Size(max=255)
	private String name;
	@NotBlank
	@Size(max=255)
	private String uuid;
	@NotNull	
	private LocalDateTime lastLogout;
	
	public RevokedToken() {		
	}

	public RevokedToken(String name, String uuid, LocalDateTime lastLogout) {
		super();
		this.name = name;
		this.uuid = uuid;
		this.lastLogout = lastLogout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getLastLogout() {
		return lastLogout;
	}

	public void setLastLogout(LocalDateTime lastLogout) {
		this.lastLogout = lastLogout;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(lastLogout, name, uuid);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevokedToken other = (RevokedToken) obj;
		return Objects.equals(lastLogout, other.lastLogout) && Objects.equals(name, other.name)
				&& Objects.equals(uuid, other.uuid);
	}
}
