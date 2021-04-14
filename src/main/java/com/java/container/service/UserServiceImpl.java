package com.java.container.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.container.dao.UserRegistrationDAO;
import com.java.container.entity.Role;
import com.java.container.entity.User;
import com.java.container.repository.UserReposiroty;

@Service
public class UserServiceImpl implements UserService{
	
	private UserReposiroty userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserReposiroty userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
	public User save(UserRegistrationDAO registrationDAO) {
		User user = new User(registrationDAO.getUsername(),
				passwordEncoder.encode(registrationDAO.getPassword()), 
				registrationDAO.getFullname(), 
				registrationDAO.getEmail(), 
				registrationDAO.getGender(),
				registrationDAO.getCreate_at(), 
				Arrays.asList(new Role("USER")));
		return userRepository.save(user);
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username);
		if(user == null) {
			System.out.println("Username can't be found or invalid");
			throw new UsernameNotFoundException("Username can't be found in database or invalid");
		}
		System.out.println("Seems like you logged in huh?");
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole_name())).collect(Collectors.toList());
		
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(long id) {
		Optional<User> optional = userRepository.findById(id);
		User user = null;
		if(optional .isPresent()) {
			user = optional.get();
		}
		else{
			throw new RuntimeException("User not found for id :: " + id);
		}
		return user;
	}

	@Override
	public void deleteUserById(Long id) {
		this.userRepository.deleteById(id);
		
	}
	@Override
	public Page<User> findPaginated(int pageNum, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending():
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNum -1, pageSize, sort);
		return this.userRepository.findAll(pageable);
	}
}
