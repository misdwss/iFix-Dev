package org.egov.util;

import org.egov.PspclIfixAdapterApplication;
import org.egov.config.AbstractPostgreSQLTestContainerIT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;

import static org.egov.util.PspclIfixAdapterConstant.PATH_FETCH_PSPCL_BILL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(classes = PspclIfixAdapterApplication.class)
//@AutoConfigureEmbeddedDatabase
class PspclIfixAdapterUtilTest extends AbstractPostgreSQLTestContainerIT {

    @InjectMocks
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;

    @Mock
    private ResourceLoader resourceLoader;

    @Test
    void testGetFileAsString() {
        assertEquals("", this.pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_BILL));
        assertEquals("",
                this.pspclIfixAdapterUtil.getFileAsString("Exception occurred while reading the file from filePath : {}"));
        assertEquals("", this.pspclIfixAdapterUtil.getFileAsString(""));
        assertEquals("", this.pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_BILL));
        assertEquals("",
                this.pspclIfixAdapterUtil.getFileAsString("Exception occurred while reading the file from filePath : {}"));
        assertEquals("", this.pspclIfixAdapterUtil.getFileAsString(""));
    }
}

