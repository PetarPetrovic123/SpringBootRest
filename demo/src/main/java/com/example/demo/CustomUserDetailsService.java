package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements  UserDetailsService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    
    @Lazy
    @Autowired
    AuthenticationManager authManager;

    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Ime2> ime =  userRepository.findByUsername(username);
        if(!ime.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }

        return User
        .withUsername(ime.get().getUsername())
        .password(ime.get().getPassword())
        .roles(ime.get().getRole())
        .build();
    }

    public Ime2 saveUser(Ime2 ime){
        String encodedPassword = passwordEncoder.encode(ime.getPassword());
        ime.setPassword(encodedPassword);
        userRepository.save(ime);
        return ime;
    }

    public List<Ime2> listaImena(){
        return userRepository.findAll();
    }

    public void deleteUser(int id){
        userRepository.deleteById(id);
    }

    public Optional<Ime2> findUserById(int id){
        return userRepository.findById(id);
    }

    public List<Ime2> findByRole(String role){
        return userRepository.findByRole(role);
    }

    public Optional<Ime2> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public String verify(Ime2 ime) {
        Authentication authentication = 
        authManager.authenticate(new UsernamePasswordAuthenticationToken(ime.getUsername(), ime.getPassword()));

        if(authentication.isAuthenticated()) return jwtService.generateToken(ime.getUsername());

        return "failure";
    }
    
}
