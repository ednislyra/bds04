package com.devsuperior.bds04.service;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.RoleDTO;
import com.devsuperior.bds04.dto.UserDTO;
import com.devsuperior.bds04.dto.UserInsertDTO;
import com.devsuperior.bds04.dto.UserUpdateDTO;
import com.devsuperior.bds04.entities.Role;
import com.devsuperior.bds04.entities.User;
import com.devsuperior.bds04.repositories.RoleRepository;
import com.devsuperior.bds04.repositories.UserRepository;
import com.devsuperior.bds04.service.exception.DatabaseException;
import com.devsuperior.bds04.service.exception.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = userRepository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		try {
			Optional<User> obj = userRepository.findById(id);
			User user = obj.orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
			return new UserDTO(user);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Not found id: " + id);
		}
	}

	@Transactional(readOnly = false)
	public UserDTO insert(UserInsertDTO dto) {
		User user = new User();
		copyDtoToEntity(dto, user);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user = userRepository.save(user);
		return new UserDTO(user);

	}

	@Transactional(readOnly = false)
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User user = userRepository.getOne(id);
			copyDtoToEntity(dto, user);
			return new UserDTO(user);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Not found id: " + id);
		}
	}

	public void deleteUserById(Long id) {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Not found id: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation trying to delete " + id);
		}
	}

	private void copyDtoToEntity(UserDTO dto, User user) {
		user.setEmail(dto.getEmail());
		user.getRoles().clear();
		for (RoleDTO roleDTO : dto.getRoles()) {

			Role role = roleRepository.getOne(roleDTO.getId());
			user.getRoles().add(role);
		}

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			logger.error("User not found: " + username);
			throw new UsernameNotFoundException("Email not found!");
		}
		logger.info("User found: " + username);
		return user;
	}

}
