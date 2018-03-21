package ch.xxx.moviemanager.service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.model.User;
import ch.xxx.moviemanager.repository.CrudUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private CrudUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(username);
		if(user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", username));
		}
		List<SimpleGrantedAuthority> authorities = Pattern.compile(",").splitAsStream(user.getRoles()).map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
		return userDetails;
	}

}
