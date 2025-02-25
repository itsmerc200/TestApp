package com.quizserver.service.test;


import com.quizserver.dto.QuestionDTO;
import com.quizserver.dto.TestDTO;
import org.springframework.stereotype.Service;

@Service
public interface TestService {

    TestDTO createtest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);
}
