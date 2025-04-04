    package com.quizserver.controller;


    import com.quizserver.dto.QuestionDTO;
    import com.quizserver.dto.SubmitTestDTO;
    import com.quizserver.dto.TestDTO;
    import com.quizserver.service.test.TestService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import java.io.InputStream;


    @RestController
    @RequestMapping("api/test")
    @CrossOrigin("*")
    public class TestController {

        @Autowired

        private TestService testService;

        @GetMapping("/question/{questionId}")
        public ResponseEntity<?> getQuestionById(@PathVariable Long questionId) {
            try {
                return new ResponseEntity<>(testService.getQuestionById(questionId), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }



        @PutMapping("/{testId}")
        public ResponseEntity<?> updateTest(@PathVariable Long testId, @RequestBody TestDTO dto) {
            try {
                return new ResponseEntity<>(testService.updateTest(testId, dto), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        @PutMapping("/question/{questionId}")
        public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionDTO dto) {
            try {
                return new ResponseEntity<>(testService.updateQuestion(questionId, dto), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }


        @PostMapping("/upload-questions/{testId}")
        public ResponseEntity<?> uploadQuestions(@PathVariable Long testId, @RequestParam("file") MultipartFile file) {
            try {
                InputStream inputStream = file.getInputStream();
                testService.addQuestionsFromExcel(testId, inputStream);
                return new ResponseEntity<>("Questions uploaded successfully", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        @PostMapping
        public ResponseEntity<?> createTest(@RequestBody TestDTO dto){

            try{
                return new ResponseEntity<>(testService.createtest(dto), HttpStatus.OK);

            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }

        }


        @PostMapping("/question")
        public ResponseEntity<?> addQuestionInTest(@RequestBody QuestionDTO dto){

            try {

               return new ResponseEntity<>(testService.addQuestionInTest(dto), HttpStatus.CREATED);

            }catch (Exception e){

                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }

        }


        @GetMapping
        public ResponseEntity<?>  getAllTest(){

            try{
                return new ResponseEntity<>(testService.getAllTests(), HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
            }
        }

        @GetMapping("/{id}")
        public ResponseEntity<?>  getAllQuestions(@PathVariable Long id){

            try{
                return new ResponseEntity<>(testService.getAllQuestionsByTest(id), HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
            }
        }

        @PostMapping("/submit-test")
        public ResponseEntity<?> submitTest(@RequestBody SubmitTestDTO dto){

            try{
                return new ResponseEntity<>(testService.submitTest(dto), HttpStatus.OK);

            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }

        }

        @GetMapping("/test-result")
        public ResponseEntity<?> getAllResults(){

            try{
                return new ResponseEntity<>(testService.getAllTestResult(), HttpStatus.OK);

            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }
        }

        @GetMapping("/test-result/{id}")
        public ResponseEntity<?> getAllTestResultOfUser(@PathVariable Long id){

            try{
                return new ResponseEntity<>(testService.getAllTestResultOfUser(id), HttpStatus.OK);

            }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }
        }

        @DeleteMapping("/question/{id}")
        public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
            try {
                testService.deleteQuestion(id);
                return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        @DeleteMapping("/{testId}")
        public ResponseEntity<String> deleteTest(@PathVariable Long testId) {
            testService.deleteTest(testId);
            return ResponseEntity.ok("Test deleted successfully!");
        }
    }
