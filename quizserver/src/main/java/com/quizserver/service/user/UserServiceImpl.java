package com.quizserver.service.user;


import com.quizserver.entities.User;
import com.quizserver.enums.userRole;
import com.quizserver.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired

    private UserRepository userRepository;


    @PostConstruct
    private void createAdminUser(){
        User optionalUser = userRepository. findByRole(userRole.ADMIN);
        if(optionalUser == null){
            User user = new User();

            user.setName("Admin");
            user.setEmail("admin@gmail.com");
            user.setRole(userRole.ADMIN);
            user.setPassword("admin");

            userRepository.save(user);

        }
    }

    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email) != null;
    }
}
