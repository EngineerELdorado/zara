package com.zara.Zara.exceptions.exceptions;

public class ExcelParsingException extends RuntimeException {

    public ExcelParsingException(){
        super("Error parsing the Excel file");
    }
}
