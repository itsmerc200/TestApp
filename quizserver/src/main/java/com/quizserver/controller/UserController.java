package com.quizserver.controller;

import com.quizserver.entities.User;
import com.quizserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/auth")
@CrossOrigin("*")
public class UserController {

@Autowired
private UserService userService;


@PostMapping("/sign-up")
public ResponseEntity<?> signupUser(@RequestBody User user){
    if (userService.hasUserWithEmail(user.getEmail())){
        return new ResponseEntity<>("User Alredy Exist", HttpStatus.NOT_ACCEPTABLE);

    }

    User createUser = userService.createUser(user);

    if(createUser == null){

        return new ResponseEntity<>("User not created, Come Again Later", HttpStatus.NOT_ACCEPTABLE);

    }
    return  new ResponseEntity<>(createUser,HttpStatus.OK);
}


    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody User user){
    User dbUser = userService.login(user);

    if (dbUser == null)
        return new ResponseEntity<>("Wrong Cradiantials", HttpStatus.NOT_ACCEPTABLE);
    return new ResponseEntity<>(dbUser, HttpStatus.OK);
    }

}
