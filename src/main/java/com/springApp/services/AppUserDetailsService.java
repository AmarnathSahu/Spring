package com.springApp.services;

import com.springApp.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.find(username);
        return  user;
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
//		User user = userService.findById(id).orElseThrow(
//				() -> new UsernameNotFoundException("User not found with id : " + id)
//		);

        return userService.findById(id);
    }

}
