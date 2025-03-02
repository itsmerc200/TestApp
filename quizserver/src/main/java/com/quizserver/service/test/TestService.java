package com.quizserver.service.test;


import com.quizserver.dto.*;
import com.quizserver.entities.Test;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TestService {

    TestDTO createtest(TestDTO dto);

    QuestionDTO addQuestionInTest(QuestionDTO dto);

    List<TestDTO> getAllTests();

    TestDetailsDTO getAllQuestionsByTest(Long id);

    TestResultDTO submitTest(SubmitTestDTO request);
}
