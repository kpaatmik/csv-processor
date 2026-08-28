package com.kpaatmik.csv_processing_system.service;

import com.kpaatmik.csv_processing_system.dto.HeaderMapping;
import com.kpaatmik.csv_processing_system.exception.HeaderValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HeaderValidatorTest {

    private final HeaderValidator headerValidator =
            new HeaderValidator();

    @Test
    void shouldAcceptValidHeaders() {

        
        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "lastName", 1,
                        "email", 2,
                        "zipcode", 3,
                        "phone1", 4
                );

        
        HeaderMapping mapping =
                headerValidator.validate(headers);

        
        assertNotNull(mapping);

        assertEquals(
                "firstName",
                mapping.firstNameColumn()
        );

        assertEquals(
                "lastName",
                mapping.lastNameColumn()
        );

        assertEquals(
                "email",
                mapping.emailColumn()
        );

        assertEquals(
                "zipcode",
                mapping.zipCodeColumn()
        );

        assertEquals(
                "phone1",
                mapping.phone1Column()
        );
    }

    @Test
    void shouldAcceptWhenOnlyFirstNameIsPresent() {

        
        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "email", 1,
                        "zipcode", 2,
                        "phone1", 3
                );

         
        assertDoesNotThrow(() ->
                headerValidator.validate(headers)
        );
    }

    @Test
    void shouldAcceptWhenOnlyLastNameIsPresent() {

        
        Map<String, Integer> headers =
                Map.of(
                        "lastName", 0,
                        "email", 1,
                        "zipcode", 2,
                        "phone1", 3
                );

         
        assertDoesNotThrow(() ->
                headerValidator.validate(headers)
        );
    }

    @Test
    void shouldAcceptWhenOnlyPhone1IsPresent() {

        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "email", 1,
                        "zipcode", 2,
                        "phone1", 3
                );

        assertDoesNotThrow(() ->
                headerValidator.validate(headers)
        );
    }

    @Test
    void shouldAcceptWhenOnlyPhone2IsPresent() {

        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "email", 1,
                        "zipcode", 2,
                        "phone2", 3
                );

        assertDoesNotThrow(() ->
                headerValidator.validate(headers)
        );
    }

    @Test
    void shouldAcceptAlternativeHeaderNames() {

        
        Map<String, Integer> headers =
                Map.of(
                        "first_name", 0,
                        "email_address", 1,
                        "postal_code", 2,
                        "mobile", 3
                );

        
        HeaderMapping mapping =
                headerValidator.validate(headers);

        
        assertEquals(
                "first_name",
                mapping.firstNameColumn()
        );

        assertEquals(
                "email_address",
                mapping.emailColumn()
        );

        assertEquals(
                "postal_code",
                mapping.zipCodeColumn()
        );

        assertEquals(
                "mobile",
                mapping.phone1Column()
        );
    }

    @Test
    void shouldRejectHeadersWithoutEmail() {

        
        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "zipcode", 1,
                        "phone1", 2
                );

        
        HeaderValidationException exception =
                assertThrows(
                        HeaderValidationException.class,
                        () -> headerValidator.validate(headers)
                );

        
        assertEquals(
                "CSV must contain an email column",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectHeadersWithoutZipCode() {

        
        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "email", 1,
                        "phone1", 2
                );

        HeaderValidationException exception =
                assertThrows(
                        HeaderValidationException.class,
                        () -> headerValidator.validate(headers)
                );

        assertEquals(
                "CSV must contain a ZIP code column",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectHeadersWithoutNameColumn() {

        
        Map<String, Integer> headers =
                Map.of(
                        "email", 0,
                        "zipcode", 1,
                        "phone1", 2
                );

        HeaderValidationException exception =
                assertThrows(
                        HeaderValidationException.class,
                        () -> headerValidator.validate(headers)
                );

        assertEquals(
                "CSV must contain either first name or last name",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectHeadersWithoutPhoneColumn() {

        
        Map<String, Integer> headers =
                Map.of(
                        "firstName", 0,
                        "email", 1,
                        "zipcode", 2
                );

        HeaderValidationException exception =
                assertThrows(
                        HeaderValidationException.class,
                        () -> headerValidator.validate(headers)
                );

        assertEquals(
                "CSV must contain either phone1 or phone2",
                exception.getMessage()
        );
    }
}