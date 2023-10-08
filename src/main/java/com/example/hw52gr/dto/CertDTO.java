package com.example.hw52gr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class CertDTO {

  private String name;
  private long expirationDays;

}
