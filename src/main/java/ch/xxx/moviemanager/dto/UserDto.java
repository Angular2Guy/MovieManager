package ch.xxx.moviemanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
	private Long id;
	private String username;
	private String password;
	private String moviedbkey;
	private String roles;
	
	public UserDto() {		
	}
	
	public UserDto(Long id, String username, String password, String moviedbkey, String roles) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.moviedbkey = moviedbkey;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMoviedbkey() {
		return moviedbkey;
	}
	public void setMoviedbkey(String moviedbkey) {
		this.moviedbkey = moviedbkey;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "UserDto [id=" + id + ", username=" + username + ", password=" + password + ", moviedbkey=" + moviedbkey
				+ ", roles=" + roles + "]";
	}
	
}
