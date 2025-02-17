package com.quizserver.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Setter
@Getter
public class TestDTO {


    private Long id;

    private String title;


    private String description;


    private Long time;



}
