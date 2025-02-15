package com.quizserver.service.test;

import com.quizserver.repository.TestRepository;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    private TestRepository testRepository;
}
