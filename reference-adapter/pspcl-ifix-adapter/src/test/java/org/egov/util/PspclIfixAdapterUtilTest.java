package org.egov.util;

import org.egov.PspclIfixAdapterApplication;
import org.egov.config.AbstractPostgreSQLTestContainerIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;

import static org.egov.util.PspclIfixAdapterConstant.PATH_FETCH_PSPCL_BILL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(classes = PspclIfixAdapterApplication.class)
//@AutoConfigureEmbeddedDatabase
class PspclIfixAdapterUtilTest /*extends AbstractPostgreSQLTestContainerIT */{

//    @Mock
//    private PspclIfixAdapterUtil pspclIfixAdapterUtil;
//
//    @Mock
//    private ResourceLoader resourceLoader;
//
//    @Test
//    void testGetFileAsString() {
//        //PspclIfixAdapterUtil pspclIfixAdapterUtil = new PspclIfixAdapterUtil();
//        assertEquals("", pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_BILL));
//        assertEquals("",
//                pspclIfixAdapterUtil.getFileAsString("Exception occurred while reading the file from filePath : {}"));
//        assertEquals("", pspclIfixAdapterUtil.getFileAsString(""));
//        assertEquals("", pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_BILL));
//        assertEquals("",
//                pspclIfixAdapterUtil.getFileAsString("Exception occurred while reading the file from filePath : {}"));
//        assertEquals("", pspclIfixAdapterUtil.getFileAsString(""));
//    }
}

