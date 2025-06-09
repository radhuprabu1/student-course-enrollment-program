package com.example.enrollment.course.dto.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterStudentRequest {
    private String firstName;
    private String lastName;
    private String contactNumber;
}
