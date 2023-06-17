package com.think.reactor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User entity
 *
 * @author veione
 * @version 1.0.0
 * @date 2023年06月17日 09:37:00
 */
@Data
@ToString
@EqualsAndHashCode(of = {"id", "name", "department"})
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "users")
public class User {
    @Id
    private String id;
    private String name;
    private int age;
    private double salary;
    private String department;
}
