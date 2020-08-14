package com.example.tformattertest.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TFormatterVO {
    private int lineNumber;
    private String lineContents;
}
