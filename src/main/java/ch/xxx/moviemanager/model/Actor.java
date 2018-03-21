package ch.xxx.moviemanager.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Actor {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="actorid")
	private int actorId;
	private String name;
	private Integer gender;
	private Date birthday;
	private Date deathday;
	private String biography;
	@Column(name="placeofbirth")
	private String placeOfBirth;
	@OneToMany(mappedBy="actor", cascade = CascadeType.ALL, orphanRemoval=true)	
	private List<Cast> casts = new ArrayList<>();
	@ManyToMany
	@JoinTable(name = "Actor_User", 
		joinColumns = @JoinColumn(name = "actor_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users = new ArrayList<>();
	
	public List<User> getUsers() {
		return users;
	}
	public int getActorId() {
		return actorId;
	}
	public void setActorId(int actorId) {
		this.actorId = actorId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
}
