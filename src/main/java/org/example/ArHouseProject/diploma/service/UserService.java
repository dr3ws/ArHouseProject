package org.example.ArHouseProject.diploma.service;

import org.example.ArHouseProject.diploma.models.WorksModel;
import org.example.ArHouseProject.diploma.repository.RolesRepository;
import org.example.ArHouseProject.diploma.repository.WorksRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService
{
    final WorksRepository userRepository;
    final RolesRepository roleRepository;
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(WorksRepository userRepository, RolesRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WorksModel user = userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return user;
    }

    public void saveUser(WorksModel user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void saveEditUser(WorksModel user, String pass) {
        if (Objects.equals(user.getPassword(), ""))
            user.setPassword(pass);
        else
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
}