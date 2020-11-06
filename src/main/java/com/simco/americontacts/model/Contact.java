package com.simco.americontacts.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class Contact {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

}
