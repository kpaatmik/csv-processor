package com.kpaatmik.csv_processing_system.dto;

public record HeaderMapping( 
		String firstNameColumn,
        String lastNameColumn,
        String emailColumn,
        String zipCodeColumn,
        String phone1Column,
        String phone2Column) 
{

}
