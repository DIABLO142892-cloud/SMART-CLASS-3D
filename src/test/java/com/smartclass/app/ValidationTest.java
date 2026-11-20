package com.smartclass.app;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
    @Test void requiredTrimsAndAcceptsValidValue(){ assertEquals("hello",Validation.required("  hello  ","name",10)); }
    @Test void requiredRejectsBlank(){ assertEquals(400,assertThrows(AppException.class,()->Validation.required(" ","name",10)).status()); }
    @Test void requiredRejectsLongValue(){ assertThrows(AppException.class,()->Validation.required("123456","code",5)); }
    @Test void positiveIntAcceptsNumber(){ assertEquals(25,Validation.positiveInt("25","capacity")); }
    @Test void positiveIntRejectsZero(){ assertThrows(AppException.class,()->Validation.positiveInt("0","capacity")); }
    @Test void positiveIntRejectsText(){ assertThrows(AppException.class,()->Validation.positiveInt("many","capacity")); }
    @Test void dateTimeParsesBrowserFormat(){ assertEquals(LocalDateTime.of(2026,9,10,10,30),Validation.dateTime("2026-09-10T10:30","start")); }
    @Test void dateTimeRejectsInvalid(){ assertThrows(AppException.class,()->Validation.dateTime("tomorrow","start")); }
    @Test void roleNormalizesCase(){ assertEquals("LECTURER",Validation.role("lecturer")); }
    @Test void roleRejectsUnknown(){ assertThrows(AppException.class,()->Validation.role("guest")); }
    @Test void roomStatusNormalizesCase(){ assertEquals("MAINTENANCE",Validation.roomStatus("maintenance")); }
    @Test void roomStatusRejectsUnknown(){ assertThrows(AppException.class,()->Validation.roomStatus("closed")); }
}
