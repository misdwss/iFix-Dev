package org.egov.util;

import client.stub.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.advice.LogExecutionTime;
import org.egov.config.PspclIfixAdapterConfiguration;
import org.egov.repository.SoapServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.egov.util.PspclIfixAdapterConstant.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class PspclUtil {

    @Autowired
    private PspclIfixAdapterConfiguration pspclIfixAdapterConfiguration;

    @Autowired
    private PspclIfixAdapterUtil pspclIfixAdapterUtil;

    @Autowired
    private SoapServiceRequestRepository soapServiceRequestRepository;

    @Autowired
    private XmlMapper xmlMapper;

    @Autowired
    private ObjectMapper objectMapper;



    @LogExecutionTime
    public List<BillResultData> getBillsFromPspcl(String accountNumber) {
        try {
            String plaintext= pspclIfixAdapterConfiguration.getPspclUsername() + SEPERATOR
                    + pspclIfixAdapterConfiguration.getPspclPassword() + SEPERATOR
                    + accountNumber + SEPERATOR
                    + NOT_APPLICABLE + SEPERATOR
                    + NOT_APPLICABLE + SEPERATOR
                    + SEARCH_BY_ACCOUNT_NUMBER;
            String saltKey =pspclIfixAdapterConfiguration.getPspclSaltkey();
            String hashedSequence = encryptStringUsingAES256Padding7(plaintext,saltKey);
            String reqFetchPspclBill = pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_BILL_NEW);
            if (StringUtils.isBlank(reqFetchPspclBill)) {
                return Collections.emptyList();
            }
            reqFetchPspclBill = reqFetchPspclBill.replace(PLACEHOLDER_ACCOUNT_NO, hashedSequence);
            reqFetchPspclBill =reqFetchPspclBill.replace(PLACEHOLDER_USERNAME, pspclIfixAdapterConfiguration.getPspclUsername());


            String outputString = soapServiceRequestRepository.fetchResult(reqFetchPspclBill,
                    pspclIfixAdapterConfiguration.getUrlForFetchPspclBill()).toString();

            if (StringUtils.isBlank(outputString)) {
                log.debug("There is no response data from PSPCL system for fetch Bills");
                return Collections.emptyList();
            }

            JsonNode root = xmlMapper.readTree(outputString.getBytes());
            String nodeArray = root.findValue(TAG_GET_BILLS_PSPCL_REPONSE).asText();

            RequestRecentBillResult requestRecentBillResult = objectMapper.readValue(nodeArray, RequestRecentBillResult.class);
            return Arrays.asList(requestRecentBillResult.getResponseData());
        } catch (Exception ex) {
            log.error("Exception occurred while getting bill details from PSPCL system", ex);
        }
        return Collections.emptyList();
    }

    @LogExecutionTime
    public List<PaymentsResultData> getPaymentsFromPspcl(String accountNumber) {
        try {
            String reqFetchPspclPayment = pspclIfixAdapterUtil.getFileAsString(PATH_FETCH_PSPCL_PAYMENT_NEW);
            String plaintext= pspclIfixAdapterConfiguration.getPspclUsername() + SEPERATOR
                    + pspclIfixAdapterConfiguration.getPspclPassword() + SEPERATOR
                    + accountNumber + SEPERATOR
                    + NOT_APPLICABLE + SEPERATOR
                    + NOT_APPLICABLE + SEPERATOR
                    + SEARCH_BY_ACCOUNT_NUMBER;
            String saltKey =pspclIfixAdapterConfiguration.getPspclSaltkey();
            String hashedSequence = encryptStringUsingAES256Padding7(plaintext,saltKey);
            if (StringUtils.isBlank(reqFetchPspclPayment)) {
                return Collections.emptyList();
            }
            reqFetchPspclPayment = reqFetchPspclPayment.replace(PLACEHOLDER_ACCOUNT_NO, hashedSequence);
            reqFetchPspclPayment =reqFetchPspclPayment.replace(PLACEHOLDER_USERNAME, pspclIfixAdapterConfiguration.getPspclUsername());
            String outputString = soapServiceRequestRepository.fetchResult(reqFetchPspclPayment,
                    pspclIfixAdapterConfiguration.getUrlForFetchPspclPayment()).toString();

            if (StringUtils.isBlank(outputString)) {
                log.debug("There is no response data from PSPCL system for fetch Payments");
                return Collections.emptyList();
            }

            JsonNode root = xmlMapper.readTree(outputString.getBytes());
            String nodeArray = root.findValue(TAG_GET_PAYMENTS_PSPCL_REPONSE).asText();

            RequestRecentPaymentsResult requestRecentPaymentsResult = objectMapper.readValue(nodeArray, RequestRecentPaymentsResult.class);
            return Arrays.asList(requestRecentPaymentsResult.getResponseData());
        } catch (Exception ex) {
            log.error("Exception occurred while getting payment details from PSPCL system", ex);
        }
        return Collections.emptyList();
    }

    private static String encryptStringUsingAES256(String plainText, String salt) throws Exception {
        if (plainText == null || plainText.length() <= 0)
            throw new IllegalArgumentException("NULL_plainText");
        if (salt == null || salt.length() <= 0)
            throw new IllegalArgumentException("NULL_salt");

        String encrypted;
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        // Create an AesManaged object with the specified salt.
        SecretKey secretKey = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(salt.getBytes(StandardCharsets.UTF_8));
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // Create the streams used for encryption.
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, aesCipher);
             OutputStreamWriter writer = new OutputStreamWriter(cipherOutputStream, StandardCharsets.UTF_8)) {
            // Write all data to the stream.
            writer.write(plainText);
            writer.flush();
            encrypted = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new Exception("Error encrypting the string: " + e.getMessage());
        }

        // Return the encrypted string
        return encrypted;
    }

    public static String encrypt(String value, String saltkey) {
        String plainText = value;
        String escapedString;
        try {
            byte[] key = saltkey.getBytes("UTF-8");
            byte[] ivs = saltkey.getBytes();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
            escapedString = Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8"))).trim();

            return escapedString;
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }
    private static String encryptStringUsingAES256Padding7(String plainText, String salt) {
        if (plainText == null || plainText.length() <= 0)
            throw new IllegalArgumentException("NULL_plainText");
        if (salt == null || salt.length() <= 0)
            throw new IllegalArgumentException("NULL_salt");

        try {
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

            Security.addProvider(new BouncyCastleProvider());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec secretKeySpec = new SecretKeySpec(saltBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(saltBytes);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public static void getRegisteredAccountNumbers(String mobile) {
        String data = "username|<password>|NA|" + mobile + "|NA|2";
        String encryptedData = encryptHelper(data, KEY, IV);

        Map<String, Object> finalsyncArray = new HashMap<>();
        finalsyncArray.put("userName", "username");
        finalsyncArray.put("hashedData", encryptedData);

        try {
            // Create a SOAP client
            SoapClient client = new SoapClient(WSDL_URL);

            // Set SOAP headers
            client.setSoapAction("http://tempuri.org/I<pagename>/requestRecentBill");
            client.setAddress("https://api.pspcl.in/services/<pagename>.svc");

            // Call the requestRecentBill method with requestJSON parameter
            Map<String, Object> request = new HashMap<>();
            request.put("requestJSON", finalsyncArray.toString());
            client.callMethod("requestRecentBill", request);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static String encryptHelper(String inputString, String key, String iv)
            throws GeneralSecurityException {
        Key secretKey = new SecretKeySpec(Base64.decodeBase64(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.decodeBase64(iv));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(inputString.getBytes());

        return Base64.encodeBase64String(encryptedBytes);
    }*/

}

