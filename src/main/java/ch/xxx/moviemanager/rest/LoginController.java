package ch.xxx.moviemanager.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.moviemanager.dto.GenereDto;
import ch.xxx.moviemanager.dto.UserDto;
import ch.xxx.moviemanager.service.AppUserDetailsService;
import ch.xxx.moviemanager.service.MovieManagerService;

@RestController
@RequestMapping("rest/user")
public class LoginController {
	@Autowired
	private AppUserDetailsService auds;
	@Autowired
	private MovieManagerService mmService;
	@Autowired
	private MovieManagerService service;
	private final List<GrantedAuthority> AUTHORITIES = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> postLogin(@RequestBody UserDto userDto) throws InterruptedException {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		try {
			UserDetails userDetails = this.auds.loadUserByUsername(userDto.getUsername());
			if (userDto.getUsername().equals(userDetails.getUsername())
					&& encoder.matches(userDto.getPassword(), userDetails.getPassword())) {
				Authentication result1 = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
						userDetails.getPassword(), AUTHORITIES);
				SecurityContextHolder.getContext().setAuthentication(result1);
				return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.ACCEPTED);
			}
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> postSignin(@RequestBody UserDto userDto) throws InterruptedException {
		if (!this.mmService.saveUser(userDto)) {
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value="/genere", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<GenereDto>> getGeneres() throws InterruptedException {
		List<GenereDto> generes = this.service.allGeneres();		
		return new ResponseEntity<List<GenereDto>>(generes, HttpStatus.OK);		
	}
	
//	@RequestMapping(value="/updateDB", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	public ResponseEntity<Boolean> getUpdateDB() throws InterruptedException {
//		boolean result = this.service.updateDB();
//		return new ResponseEntity<Boolean>( result, result ? HttpStatus.OK : HttpStatus.NOT_IMPLEMENTED);	
//	}
}
