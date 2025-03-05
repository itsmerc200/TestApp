package com.quizserver.service.test;

import com.quizserver.dto.*;
import com.quizserver.entities.Question;
import com.quizserver.entities.Test;
import com.quizserver.entities.TestResult;
import com.quizserver.entities.User;
import com.quizserver.repository.QuestionRepository;
import com.quizserver.repository.TestRepository;
import com.quizserver.repository.TestResultRepository;
import com.quizserver.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserRepository userRepository;

        public TestDTO createtest(TestDTO dto){

            Test test = new Test();

            test.setTitle(dto.getTitle());
            test.setDescription(dto.getDescription());
            test.setTime(dto.getTime());

            return testRepository.save(test).getDto();
        }

        public QuestionDTO addQuestionInTest(QuestionDTO dto) {
            Optional<Test> optionalTest = testRepository.findById(dto.getId());
            if (optionalTest.isPresent()) {

                Question question = new Question();

                question.setTest(optionalTest.get());
                question.setQuestionText(dto.getQuestionText());
                question.setOptionA(dto.getOptionA());
                question.setOptionB(dto.getOptionB());
                question.setOptionC(dto.getOptionC());
                question.setOptionD(dto.getOptionD());
                question.setCorrectOption(dto.getCorrectOption());

                return questionRepository.save(question).getDto();


            }
            throw new EntityNotFoundException("Test Not Found");
        }

        public List<TestDTO> getAllTests(){

            return testRepository.findAll().stream()
                    .map(test -> {
                        long time = test.getTime() != null ? test.getTime() : 0L;  // Handle null case
                        test.setTime(test.getQuestions().size() * time);
                        return test.getDto();
                    })
                    .collect(Collectors.toList());

        }

        public TestDetailsDTO getAllQuestionsByTest(Long id){
            Optional<Test> optionalTest = testRepository.findById(id);

            TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
            if (optionalTest.isPresent()){

                TestDTO testDTO = optionalTest.get().getDto();
                testDTO.setTime(optionalTest.get().getTime() * optionalTest.get().getQuestions().size());

                testDetailsDTO.setTestDTO(testDTO);
                testDetailsDTO.setQuestions(optionalTest.get().getQuestions().stream().map(Question::getDto).toList());

                return testDetailsDTO;
            }

            return testDetailsDTO;
        }


        public TestResultDTO submitTest(SubmitTestDTO request){

            Test test = testRepository.findById(request.getTestId()).orElseThrow(()-> new EntityNotFoundException("Test Not Found"));

            User user = userRepository.findById(request.getUserId()).orElseThrow(()->new EntityNotFoundException("User Not Found"));


            int correctAnswer = 0;

            for(QuestionResponse response: request.getResponses()){
                Question question = questionRepository.findById(response.getQuestionId()).orElseThrow(()->new EntityNotFoundException("Question Not Found"));

                if (question.getCorrectOption().equals(response.getSelectedOption())){
                    correctAnswer++;
                }
            }

            int totalQuestoins = test.getQuestions().size();

            double percentage = ((double) correctAnswer/totalQuestoins)*100;

            TestResult testResult = new TestResult();

            testResult.setTest(test);
            testResult.setUser(user);
            testResult.setTotalQuestions(totalQuestoins);
            testResult.setCorrectAnswer(correctAnswer);
            testResult.setPercentage(percentage);

            return testResultRepository.save(testResult).getDto();


        }

        public List<TestResultDTO> getAllTestResult(){
            return testResultRepository.findAll().stream().map(TestResult::getDto).collect(Collectors.toList());
        }



    public List<TestResultDTO> getAllTestResultOfUser(Long userId){
        return testResultRepository.findAllByUserId(userId).stream().map(TestResult::getDto).collect(Collectors.toList());
    }

}
