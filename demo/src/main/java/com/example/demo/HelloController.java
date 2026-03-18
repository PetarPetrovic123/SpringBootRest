package com.example.demo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    private final CustomUserDetailsService userDetailsService;

    public HelloController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @GetMapping("/")
    public String hello(HttpServletRequest request){
        return "hellooo" + request.getSession().getId();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        List<Ime2> users = userDetailsService.listaImena();
        if(users.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Prazno");
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/users")
    public Ime2 saveUser(@RequestBody Ime2 ime){
        userDetailsService.saveUser(ime);
        return ime;
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody Ime2 ime){
        userDetailsService.saveUser(ime);
        return ResponseEntity.ok(ime);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id){
        userDetailsService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
    }

    @GetMapping("/users-role/{role}")
    public ResponseEntity<?> getByRole(@PathVariable String role){
        return ResponseEntity.ok(userDetailsService.findByRole(role));
    }

    @PostMapping("/login")
    public String login(@RequestBody Ime2 ime){
        return userDetailsService.verify(ime);
    }
}
