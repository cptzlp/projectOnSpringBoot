package ru.max.springcourse.projectonspringboot.entity;



import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Book {
    private Long id;
    private String title;
    private LocalDate publicationDate;
}
