package com.cursodesousa.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoanFilterDTO {
    private Long id;
    private String isbn;
    private String customer;
    private BookDTO book;
}
