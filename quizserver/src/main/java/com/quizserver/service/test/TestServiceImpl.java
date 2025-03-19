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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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




    @Override
    public TestDTO createtest(TestDTO dto) {
        Test test = new Test();
        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setTime(dto.getTime());
        return testRepository.save(test).getDto();
    }

    @Override
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

    @Override
    public List<TestDTO> getAllTests() {
        return testRepository.findAll().stream()
                .map(test -> {
                    long time = test.getTime() != null ? test.getTime() : 0L;  // Handle null case
                    test.setTime(test.getQuestions().size() * time);
                    return test.getDto();
                })
                .collect(Collectors.toList());
    }

    @Override
    public TestDetailsDTO getAllQuestionsByTest(Long id) {
        Optional<Test> optionalTest = testRepository.findById(id);
        TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
        if (optionalTest.isPresent()) {
            TestDTO testDTO = optionalTest.get().getDto();
            testDTO.setTime(optionalTest.get().getTime() * optionalTest.get().getQuestions().size());
            testDetailsDTO.setTestDTO(testDTO);
            testDetailsDTO.setQuestions(optionalTest.get().getQuestions().stream().map(Question::getDto).toList());
            return testDetailsDTO;
        }
        return testDetailsDTO;
    }

    @Override
    public TestResultDTO submitTest(SubmitTestDTO request) {
        Test test = testRepository.findById(request.getTestId()).orElseThrow(() -> new EntityNotFoundException("Test Not Found"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        int correctAnswer = 0;
        for (QuestionResponse response : request.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId()).orElseThrow(() -> new EntityNotFoundException("Question Not Found"));
            if (question.getCorrectOption().equals(response.getSelectedOption())) {
                correctAnswer++;
            }
        }

        int totalQuestions = test.getQuestions().size();
        double percentage = ((double) correctAnswer / totalQuestions) * 100;

        TestResult testResult = new TestResult();
        testResult.setTest(test);
        testResult.setUser(user);
        testResult.setTotalQuestions(totalQuestions);
        testResult.setCorrectAnswer(correctAnswer);
        testResult.setPercentage(percentage);

        return testResultRepository.save(testResult).getDto();
    }

    @Override
    public List<TestResultDTO> getAllTestResult() {
        return testResultRepository.findAll().stream().map(TestResult::getDto).collect(Collectors.toList());
    }

    @Override
    public List<TestResultDTO> getAllTestResultOfUser(Long userId) {
        return testResultRepository.findAllByUserId(userId).stream().map(TestResult::getDto).collect(Collectors.toList());
    }

    @Override
    public TestDTO updateTest(Long testId, TestDTO dto) {
        Optional<Test> optionalTest = testRepository.findById(testId);
        if (optionalTest.isPresent()) {
            Test test = optionalTest.get();
            test.setTitle(dto.getTitle());
            test.setDescription(dto.getDescription());
            test.setTime(dto.getTime());
            return testRepository.save(test).getDto();
        }
        throw new EntityNotFoundException("Test Not Found with ID: " + testId);
    }

    @Override
    public QuestionDTO updateQuestion(Long questionId, QuestionDTO dto) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setQuestionText(dto.getQuestionText());
            question.setOptionA(dto.getOptionA());
            question.setOptionB(dto.getOptionB());
            question.setOptionC(dto.getOptionC());
            question.setOptionD(dto.getOptionD());
            question.setCorrectOption(dto.getCorrectOption());
            return questionRepository.save(question).getDto();
        }
        throw new EntityNotFoundException("Question Not Found with ID: " + questionId);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public void deleteTest(Long testId) {
        Optional<Test> testOptional = testRepository.findById(testId);
        if (testOptional.isPresent()) {
            Test test = testOptional.get();
            if (test.getQuestions() != null && !test.getQuestions().isEmpty()) {
                questionRepository.deleteAll(test.getQuestions());
            }
            testResultRepository.deleteAll(testResultRepository.findAllByTestId(testId));
            testRepository.delete(test);
        } else {
            throw new EntityNotFoundException("Test Not Found with ID: " + testId);
        }
    }

    @Override
    public void addQuestionsFromExcel(Long testId, InputStream inputStream) throws Exception {
        List<Question> questions = new ArrayList<>();

        // Use try-with-resources to ensure the workbook and input stream are closed
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row (if present)
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            Test test = testRepository.findById(testId).orElseThrow(() -> new EntityNotFoundException("Test Not Found"));

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Skip empty rows
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().isEmpty()) {
                    continue;
                }

                Question question = new Question();
                question.setTest(test);

                // Question Text (Column 0)
                question.setQuestionText(getCellValueAsString(row.getCell(0)));

                // Option A (Column 1)
                question.setOptionA(getCellValueAsString(row.getCell(1)));

                // Option B (Column 2)
                question.setOptionB(getCellValueAsString(row.getCell(2)));

                // Option C (Column 3)
                question.setOptionC(getCellValueAsString(row.getCell(3)));

                // Option D (Column 4)
                question.setOptionD(getCellValueAsString(row.getCell(4)));

                // Correct Answer (Column 5)
                question.setCorrectOption(getCellValueAsString(row.getCell(5)));

                questions.add(question);
            }

            // Save all questions to the database
            questionRepository.saveAll(questions);
        }
    }

    // Helper method to safely get cell value as String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Return empty string for null cells
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // Handle numeric values
            default:
                return "";
        }
    }

    @Override
    public QuestionDTO getQuestionById(Long questionId) {
        // Fetch the question from the repository
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        // Map the Question entity to a QuestionDTO
        return mapQuestionToDTO(question);
    }

    private QuestionDTO mapQuestionToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectOption(question.getCorrectOption());
        return dto;
    }

}