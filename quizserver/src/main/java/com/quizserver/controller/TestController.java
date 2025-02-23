package com.quizserver.controller;


import com.quizserver.dto.TestDTO;
import com.quizserver.service.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/test")
@CrossOrigin("*")
public class TestController {

    @Autowired

    private TestService testService;


    @PostMapping
    public ResponseEntity<?> createTest(@RequestBody TestDTO dto){

        try{
            return new ResponseEntity<>(testService.createtest(dto), HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }
}
