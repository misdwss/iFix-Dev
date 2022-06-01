package org.egov.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.config.PspclIfixAdapterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.egov.util.PspclIfixAdapterConstant.DEFAULT_DATE_FORMAT;

@Component
@Slf4j
public class PspclIfixAdapterUtil {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private PspclIfixAdapterConfiguration adapterConfiguration;

    public String getFileAsString(String filePath) {
        String reqFetchPspclBill = "";
        try {
            if (StringUtils.isBlank(filePath))
                return reqFetchPspclBill;
            URI filePathUri = resourceLoader.getResource(filePath) != null ? resourceLoader.getResource(filePath).getURI() : null;
            reqFetchPspclBill = filePathUri != null ? Files.lines(Paths.get(filePathUri)).collect(Collectors.joining("\n")) : reqFetchPspclBill;
        } catch (IOException e) {
            log.error("Exception occurred while reading the file from filePath : {}", filePath, e);
        }
        return reqFetchPspclBill;
    }

    public Date format(String format, String date) {
        String dateFormat = DEFAULT_DATE_FORMAT;
        if (StringUtils.isNotBlank(format)) {
            dateFormat = format;
        }
        if (StringUtils.isNotBlank(date)) {
            DateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            try {
                return (dateFormatter.parse(date));
            } catch (ParseException e) {
                log.error("Exception occurred while formatting the date : {}", date, e);
            }
        }
        return null;
    }

    public Date format(String format, Date date) {
        String dateFormat = DEFAULT_DATE_FORMAT;
        if (StringUtils.isNotBlank(format)) {
            dateFormat = format;
        }
        if (date != null) {
            DateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
            return (new Date(dateFormatter.format(date)));
        }
        return null;
    }
}
