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

    List<TestResultDTO> getAllTestResult();

    List<TestResultDTO> getAllTestResultOfUser(Long userId);

    void deleteQuestion(Long id);

    public void deleteTest(Long testId);






}
