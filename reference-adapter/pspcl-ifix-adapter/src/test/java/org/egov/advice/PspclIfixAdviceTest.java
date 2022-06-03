package org.egov.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.egov.PspclIfixAdapterApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes= PspclIfixAdapterApplication.class)
class PspclIfixAdviceTest {

    @InjectMocks
    private PspclIfixAdvice pspclIfixAdvice;


    @Test
    void testLogExecutionTimeWithDefaultValues() throws Throwable {
        PspclIfixAdvice pspclIfixAdvice = new PspclIfixAdvice();
        Signature signature = mock(Signature.class);
        when(signature.getDeclaringTypeName()).thenReturn("Declaring Type Name");
        when(signature.getName()).thenReturn("Name");
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.proceed()).thenReturn("Proceed");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        assertEquals("Proceed", pspclIfixAdvice.logExecutionTime(proceedingJoinPoint));
        verify(proceedingJoinPoint).proceed();
        verify(proceedingJoinPoint, atLeast(1)).getSignature();
        verify(signature).getDeclaringTypeName();
        verify(signature).getName();
    }
}

