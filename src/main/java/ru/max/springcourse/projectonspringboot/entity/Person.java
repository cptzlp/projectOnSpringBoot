package ru.max.springcourse.projectonspringboot.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Person {
    private Long id;
    private String name;
    private int age;
    private Long bookId;
}
