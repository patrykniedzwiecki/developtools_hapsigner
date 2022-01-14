/*
 * Copyright (c) 2021-2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohos.hapsigntool;

import com.ohos.hapsigntool.api.LocalizationAdapter;
import com.ohos.hapsigntool.api.model.Options;
import com.ohos.hapsigntool.key.KeyPairTools;
import com.ohos.hapsigntool.keystore.KeyStoreHelper;
import com.ohos.hapsigntool.profile.ProfileSignTool;
import com.ohos.hapsigntool.profile.VerifyHelper;
import com.ohos.hapsigntool.profile.model.VerificationResult;
import com.ohos.hapsigntool.utils.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ProfileTest.
 *
 * @since 2021/12/28
 */
public class ProfileTest {
    /**
     * Add log info.
     */
    private final Logger logger = LoggerFactory.getLogger(ProfileTest.class);
    /**
     * Output the signed ProvisionProfile file in p7b format.
     */
    private static final String OUT_PATH = "test_sign_profile.p7b";
    /**
     * Keystore file in JKS or P12 format.
     */
    private static final String KEY_STORE_PATH = "test_keypair.jks";
    /**
     * Key alias.
     */
    private static final String KEY_ALIAS = "oh-app1-key-v1";
    /**
     * Key pwd and keystore pwd.
     */
    private static final String PWD = "123456";
    /**
     * Input original ProvisionProfile file.
     */
    private static final String IN_FILE_PATH = "UnsgnedDebugProfileTemplate.json";
    /**
     * Profile signing certificate.
     */
    private static final String CERT_PATH = "test_profile_cert.cer";
    /**
     * Mode is localSign.
     */
    private static final String LOCAL_SIGN = "localSign";
    /**
     * Mode is remoteSign.
     */
    private static final String REMOTE_SIGN = "remoteSign";
    /**
     * Params SHA256withRSA.
     */
    public static final String SHA_256_WITH_RSA = "SHA256withRSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testProfile() throws IOException {
        try {
            Options options = new Options();
            LocalizationAdapter adapter = new LocalizationAdapter(options);
            byte[] provisionContent = ProfileSignTool.getProvisionContent(new File(adapter.getInFile()));
            byte[] p7b = ProfileSignTool.generateP7b(adapter, provisionContent);
            FileUtils.write(p7b, new File(adapter.getOutFile()));
            assertFalse(FileUtils.isFileExist(OUT_PATH));
        } catch (Exception exception) {
            logger.info(exception, () -> exception.getMessage());
        }
        loadFile(IN_FILE_PATH);
        loadFile(CERT_PATH);
        deleteFile(OUT_PATH);
        deleteFile(KEY_STORE_PATH);
        KeyPair keyPair = KeyPairTools.generateKeyPair(KeyPairTools.RSA, KeyPairTools.RSA_2048);
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(KEY_STORE_PATH, PWD.toCharArray());
        keyStoreHelper.store(KEY_ALIAS, PWD.toCharArray(), keyPair, null);
        Options options = new Options();
        putParams(options);
        LocalizationAdapter adapter = new LocalizationAdapter(options);
        byte[] provisionContent = ProfileSignTool.getProvisionContent(new File(adapter.getInFile()));
        byte[] p7b = ProfileSignTool.generateP7b(adapter, provisionContent);
        FileUtils.write(p7b, new File(adapter.getOutFile()));
        assertTrue(FileUtils.isFileExist(OUT_PATH));
        VerifyHelper verifyHelper = new VerifyHelper();
        VerificationResult verificationResult = verifyHelper.verify(p7b);
        assertTrue(verificationResult.isVerifiedPassed());
        try {
            options.put(Options.MODE, REMOTE_SIGN);
            adapter = new LocalizationAdapter(options);
            provisionContent = ProfileSignTool.getProvisionContent(new File(adapter.getInFile()));
            p7b = ProfileSignTool.generateP7b(adapter, provisionContent);
            FileUtils.write(p7b, new File(adapter.getOutFile()));
            assertTrue(FileUtils.isFileExist(OUT_PATH));
        } catch (Exception exception) {
            logger.info(exception, () -> exception.getMessage());
        }
        try {
            verificationResult = verifyHelper.verify(null);
            assertFalse(verificationResult.isVerifiedPassed());
        } catch (Exception exception) {
            logger.info(exception, () -> exception.getMessage());
        }
    }

    private void loadFile(String filePath) throws IOException {
        ClassLoader classLoader = ProfileTest.class.getClassLoader();
        InputStream fileInputStream = classLoader.getResourceAsStream(filePath);
        if (fileInputStream != null) {
            byte[] fileData = FileUtils.read(fileInputStream);
            FileUtils.write(fileData, new File(filePath));
        }
    }

    private void putParams(Options options) {
        options.put(Options.KEY_ALIAS, KEY_ALIAS);
        options.put(Options.KEY_RIGHTS, PWD.toCharArray());
        options.put(Options.MODE, LOCAL_SIGN);
        options.put(Options.PROFILE_CERT_FILE, CERT_PATH);
        options.put(Options.IN_FILE, IN_FILE_PATH);
        options.put(Options.SIGN_ALG, SHA_256_WITH_RSA);
        options.put(Options.KEY_STORE_FILE, KEY_STORE_PATH);
        options.put(Options.KEY_STORE_RIGHTS, PWD.toCharArray());
        options.put(Options.OUT_FILE, OUT_PATH);
    }

    private void deleteFile(String filePath) throws IOException {
        if (FileUtils.isFileExist(filePath)) {
            Path path = Paths.get(filePath);
            Files.delete(path);
        }
    }
}
