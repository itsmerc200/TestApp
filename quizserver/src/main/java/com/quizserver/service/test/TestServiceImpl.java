package com.quizserver.service.test;

import com.quizserver.dto.TestDTO;
import com.quizserver.entities.Test;
import com.quizserver.repository.TestRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Data
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

        public TestDTO createtest(TestDTO dto){

            Test test = new Test();
    
            test.setTitle(dto.getTitle());
            test.setDescription(dto.getDescription());
            test.setTime(dto.getTime());

            return testRepository.save(test).getDto();
        }

}
