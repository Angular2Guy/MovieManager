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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.search.engine.backend.types.TermVector;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NamedQuery(name = "Actor.count", query = "select count(a) from Actor a")
@Indexed
@Entity
public class Actor extends EntityBase {
	private Long actorId;
	@NotBlank
	@Size(max=255)
	private String name;
	private Integer gender;
	private Date birthday;
	private Date deathday;
	@FullTextField(termVector = TermVector.YES)
//	@Lob
	@Column(columnDefinition = "text")
	private String biography;
	private Double popularity = 0.0;
	private String placeOfBirth;
	@OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Cast> casts = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "actor_user", joinColumns = @JoinColumn(name = "actor_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users = new HashSet<>();

	public Set<User> getUsers() {
		return users;
	}

	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getDeathday() {
		return deathday;
	}

	public void setDeathday(Date deathday) {
		this.deathday = deathday;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public List<Cast> getCasts() {
		return casts;
	}

	public void setCasts(List<Cast> casts) {
		this.casts = casts;
	}

	public Double getPopularity() {
		return popularity;
	}

	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

}
