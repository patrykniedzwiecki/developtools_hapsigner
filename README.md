# hapsigner



#### Introduction

To ensure the integrity and secure source of applications, the applications must be signed during the build process. Only signed applications can be installed, run, and debugged on real devices. This repository provides the source code of the HAP signing tool - hapsigner. This tool can be used to generate key pairs, certificate signing requests (CSRs), certificates, profile signatures, and HAP signatures.


#### Directory Structure

    developtools_hapsigner

    ├── autosign                # One-click signature script.
	├── dist                    # SDK preconfigured file
    ├── hapsigntool             # Master code.
          ├──hap_sign_tool      # Program entry, used to verify input parameters.
          ├──hap_sign_tool_lib  # Signing tool lib, used to parse command words and parameter lists to implement logic of modules.
    ├── tools                   # Auto-test script



#### Constraints
hapsigner is developed in Java and must run in JRE 8.0 or later.
The scripts, such as the one-click signature script, are developed in Python, and must run on Python 3.x.
#### Compilation and Build

 1. Check that Gradle 7.1 has been installed.
  
        gradle -v

 2. Download the code, open the file directory **developtools_hapsigner/hapsigntool**, and run the following command to build the code:
            
        **gradle build** or **gradle jar**

 3. Check that **build/libs/hap-sign-tool.jar** (binary files) is generated in the **./hap_sign_tool/build/libs** directory.

****
#### Usage
##### Note

In the following, the JAR package used is the binary files generated during the compilation and build process.

1. Command line signatures
   Command line signatures include profile signatures and HAP signatures.

   (1) Sign a profile.

   
```shell
java -jar hap-sign-tool.jar  sign-profile -keyAlias "oh-profile1-key-v1" -signAlg "SHA256withECDSA" -mode "localSign" -profileCertFile "result\profile1.pem" -inFile "app1-profile-release.json" -keystoreFile "result\ohtest.jks" -outFile "result\app1-profile.p7b" -keyPwd "123456" -keystorePwd "123456"
```
The parameters in the command are described as follows:

    sign-profile: Sign a provisioning profile
         ├── -mode              # Signing mode, which can be localSign or remoteSign. It is mandatory.
         ├── -keyAlias          # Key alias. It is mandatory.
         ├── -keyPwd            # Key password. It is optional.
         ├── -profileCertFile   # Profile signing certificate (certificate chain, represented by level 3 certificate, level 2 certificate, and root sequence). It is mandatory.
         ├── -inFile            # Raw provisioning profile. It is mandatory.
         ├── -signAlg           # Signature algorithm, which can be SHA256withECDSA or SHA384withECDSA. It is mandatory.
         ├── -keystoreFile      # KeyStore (KS) file, in JKS or P12 format. It is mandatory if the signing mode is localSign.
         ├── -keystorePwd       # KS password. It is optional.
         ├── -outFile           # Signed provisioning profile to generate, in p7b format. It is mandatory.



(2) Sign a HAP.


```shell
java -jar hap-sign-tool.jar sign-app -keyAlias "oh-app1-key-v1" -signAlg "SHA256withECDSA" -mode "localSign" -appCertFile "result\app1.pem" -profileFile "result\app1-profile.p7b" -inFile "app1-unsigned.zip" -keystoreFile "result\ohtest.jks" -outFile "result\app1-unsigned.hap" -keyPwd "123456" -keystorePwd "123456"
```
The parameters in the command are described as follows:

    sign-app: HAP signature
         ├── -mode              # Signing mode, which can be localSign or remoteSign. It is mandatory.
         ├── -keyAlias          # Key alias. It is mandatory.
         ├── -keyPwd            # Key password. It is optional.
         ├── -appCertFile       # Application signing certificate (certificate chain, represented by level 3 certificate, level 2 certificate, and root sequence). It is mandatory.
         ├── -profileFile       # Singed provisioning profile, in p7b format. It is mandatory.
         ├── -profileSigned     # Specifies whether the profile is signed. The value 1 means signed, and value 0 means unsigned. The default value is 1. It is optional.
         ├── -inForm            # Raw file, in .zip (default) or .bin format. It is optional.
         ├── -inFile            # Raw application package, in zip or .bin format. It is mandatory.
         ├── -signAlg           # Signature algorithm, which can be SHA256withECDSA or SHA384withECDSA. It is mandatory.
         ├── -keystoreFile      # KS file, in JKS or P12 format. It is mandatory if the signing mode is localSign.
         ├── -keystorePwd       # KS password. It is optional.
         ├── -outFile           # Signed HAP file to generate. It is mandatory.
         

2. One-click signature


To improve development efficiency, this project also provides one-click signature scripts based on the hapsigner tool. You can use these scripts to easily generate key pairs and level-3 certificates and sign profiles and HAPs, instead of entering complex commands.
The scripts and configuration files are located in the **autosign** directory.

 - create_root.sh/create_root.bat
 - create_appcert_sign_profile.sh/create_appcert_sign_profile.bat
 - sign_hap.sh/sign_hap.bat
 - createAppCertAndProfile.config
 - createRootAndSubCert.config
 - signHap.config

Procedure:
1. Ensure that Python 3.5 or later has been installed.
2. Prepare **hap-sign-tool.jar**. For details, see section **Compilation and Build**.
3. Prepare the HAP to be signed and the provisioning profile template file.
4. Use the text editor to open the **createAppCertAndProfile.config** file and **signHap.config** file and change the values of **common.keyPwd** and **common.issuerKeyPwd** to match your case.
5. Run **create_appcert_sign_profile.sh** in Linux or **create_appcert_sign_profile.bat** in Windows to generate files required for signature.
6. Run **sign_hap.sh** in Linux or **sign_hap.bat** in Windows to sign the HAP.

 > Note: To generate the KS file, root CA certificate, level-2 CA certificate, and profile signing certificate, perform the following steps:
 1. Use the text editor to open the **createRootAndSubCert.config** file and change the values of **common.keyPwd** and **common.issuerKeyPwd** to match your case.
 2. Run **create_root.sh** in Linux or run **create_root.bat** in Windows to generate the required KS file, root CA certificate, level-2 CA certificate, and profile signing certificate.


****
##### Common Operations
1.Generate a key pair.

     generate-keypair: Generate a key pair.
         ├── -keyAlias          # Key alias. It is mandatory.
         ├── -keyPwd            # Key password. It is optional.
         ├── -keyAlg            # Key algorithm, which can be RSA or ECC. It is mandatory.
         ├── -keySize           # Key length. It is mandatory. The key length is 2048, 3072, or 4096 bits if RSA is used and is NIST-P-256 or NIST-P-384 if ECC is used.
         ├── -keystoreFile      # KS file, in JKS or P12 format. It is mandatory.
         ├── -keystorePwd       # KS password. It is optional.

2.Generate a CSR.

    generate-csr: generate a CSR.
         ├── -keyAlias          # Key alias. It is mandatory.
         ├── -keyPwd            # Key password. It is optional.
         ├── -subject           # Certificate subject. It is mandatory.
         ├── -signAlg           # Signature algorithm, which can be SHA256withRSA, SHA384withRSA, SHA256withECDSA, or SHA384withECDSA. It is mandatory.
         ├── -keystoreFile      # KS file, in JKS or P12 format. It is mandatory.
         ├── -keystorePwd       # KS password. It is optional.
         ├── -outFile           # CSR to generate. It is optional. If you do not specify this parameter, the CSR is output to the console.

3.Generate a root CA or subordinate CA certificate.

    generate-ca: Generate a root CA or subordinate CA certificate. If the key does not exist, generate a key together with the certificate.
         ├── -keyAlias                        # Key alias. It is mandatory.
         ├── -keyPwd                          # Key password. It is optional.
         ├── -keyAlg                          # Key algorithm, which can be RSA or ECC. It is mandatory.
         ├── -keySize                         # Key length. It is mandatory. The key length is 2048, 3072, or 4096 bits if RSA is used and is NIST-P-256 or NIST-P-384 if ECC is used.
         ├── -issuer                          # Issuer of the certificate. It is optional. It indicates a root CA certificate if this parameter is not specified.
         ├── -issuerKeyAlias                  # Key alias of the certificate issuer. It is optional. It indicates a root CA certificate if this parameter is not specified.
         ├── -issuerKeyPwd                    # Key password of the certificate issuer. It is optional.
         ├── -subject                         # Certificate subject. It is mandatory.
         ├── -validity                        # Validity period of the certificate. It is optional. The default value is 3650 days.
         ├── -signAlg                         # Signature algorithm, which can be SHA256withRSA, SHA384withRSA,  SHA256withECDSA, or SHA384withECDSA. It is mandatory.
         ├── -basicConstraintsPathLen         # Path length. It is optional. The default value is 0.
         ├── -issuerKeystoreFile              # KS file of the issuer, in JKS or P12 format. It is optional.
         ├── -issuerKeystorePwd               # KS password of the issuer. It is optional. 
         ├── -keystoreFile                    # KS file, in JKS or P12 format. It is mandatory.
         ├── -keystorePwd                     # KS password. It is optional.
         ├── -outFile                         # File to generate. It is optional. The file is output to the console if this parameter is not specified.

4.Generate an application debugging or release certificate.

    generate-app-cert: generate an application debugging or release certificate.
         ├── -keyAlias                        # Key alias. It is mandatory.
         ├── -keyPwd                          # Key password. It is optional.
         ├── -issuer                          # Issuer of the certificate. It is mandatory.
         ├── -issuerKeyAlias                  # Key alias of the certificate issuer. It is mandatory.
         ├── -issuerKeyPwd                    # Key password of the certificate issuer. It is optional.
         ├── -subject                         # Certificate subject. It is mandatory.
         ├── -validity                        # Validity period of the certificate. It is optional. The default value is 3650 days.
         ├── -signAlg                         # Signature algoritym, which can be SHA256withECDSA or SHA384withECDSA.
         ├── -keystoreFile                    # KS file, in JKS or P12 format. It is mandatory.
         ├── -keystorePwd                     # KS password. It is optional.
         ├── -issuerKeystoreFile              # KS file of the issuer, in JKS or P12 format. It is optional.
         ├── -issuerKeystorePwd               # KS password of the issuer. It is optional. 
         ├── -outForm                         # Format of the certificate to generate. The value can be cert or certChain. It is optional. The default value is certChain.
         ├── -rootCaCertFile                  # Root CA certificate, which is mandatory when outForm is certChain.
         ├── -subCaCertFile                   # Subordinate CA certificate, which is mandatory when outForm is certChain.
         ├── -outFile                         # Certificate file (certificate or certificate chain) to generate. It is optional. The file is output to the console if this parameter is not specified.

5.Generate a profile debugging or release certificate.

    generate-profile-cert: generate a profile debugging or release certificate.
         ├── -keyAlias                        # Key alias. It is mandatory.
         ├── -keyPwd                          # Key password. It is optional.
         ├── -issuer                          # Issuer of the certificate. It is mandatory.
         ├── -issuerKeyAlias                  # Key alias of the certificate issuer. It is mandatory.
         ├── -issuerKeyPwd                    # Key password of the certificate issuer. It is optional.
         ├── -subject                         # Certificate subject. It is mandatory.
         ├── -validity                        # Validity period of the certificate. It is optional. The default value is 3650 days.
         ├── -signAlg                         # Signature algoritym, which can be SHA256withECDSA or SHA384withECDSA.
         ├── -keystoreFile                    # KS file, in JKS or P12 format. It is mandatory.
         ├── -keystorePwd                     # KS password. It is optional.
         ├── -issuerKeystoreFile              # KS file of the issuer, in JKS or P12 format. It is optional.
         ├── -issuerKeystorePwd               # KS password of the issuer. It is optional. 
         ├── -outForm                         # Format of the certificate to generate. The value can be cert or certChain. It is optional. The default value is certChain.
         ├── -rootCaCertFile                  # Root CA certificate, which is mandatory when outForm is certChain.
         ├── -subCaCertFile                   # Subordinate CA certificate, which is mandatory when outForm is certChain.
         ├── -outFile                         # Certificate file (certificate or certificate chain) to generate. It is optional. The file is output to the console if this parameter is not specified.

6.Generate a common certificate, which can be used to generate a custom certificate.

    generate-cert: generate a common certificate, which can be used to generate a custom certificate.
          ├── -keyAlias                          # Key alias. It is mandatory.
          ├── -keyPwd                            # Key password. It is optional.
          ├── -issuer                            # Issuer of the certificate. It is mandatory.
          ├── -issuerKeyAlias                    # Key alias of the certificate issuer. It is mandatory.
          ├── -issuerKeyPwd                      # Key password of the certificate issuer. It is optional.
          ├── -subject                           # Certificate subject. It is mandatory.
          ├── -validity                          # Validity period of the certificate. It is optional. The default value is 1095 days.
          ├── -keyUsage                          # Usages of the key. It is mandatory. The key usages include digitalSignature, nonRepudiation, keyEncipherment,
          ├                                        dataEncipherment, keyAgreement, certificateSignature, crlSignature,
          ├                                        encipherOnly, and decipherOnly. Use a comma (,) to separate multiple values.
          ├── -keyUsageCritical                  # Specifies whether keyUsage is a critical option. It is optional. The default value is yes.
          ├── -extKeyUsage                       # Extended key usages. It is optional. The extended key usages include clientAuthentication, serverAuthentication,
          ├                                        codeSignature, emailProtection, smartCardLogin, timestamp, and ocspSignature.
          ├── -extKeyUsageCritical               # Specifies whether extKeyUsage is a critical option. It is optional. The default value is No.
          ├── -signAlg                           # Signature algorithm, which can be SHA256withRSA, SHA384withRSA,  SHA256withECDSA, or SHA384withECDSA. It is mandatory.
          ├── -basicConstraints                  # Specifies whether basicConstraints is contained. It is optional. The default value is No.
          ├── -basicConstraintsCritical          # Specifies whether basicConstraints is a critical option. It is optional. The default value is No.
          ├── -basicConstraintsCa                # Specifies whether it is CA. It is optional. The default value is No.
          ├── -basicConstraintsPathLen           # Path length. It is optional. The default value is 0.
          ├── -issuerKeystoreFile                # KS file of the issuer, in JKS or P12 format. It is optional.
          ├── -issuerKeystorePwd                 # KS password of the issuer. It is optional. 
          ├── -keystoreFile                      # KS file, in JKS or P12 format. It is mandatory.
          ├── -keystorePwd                       # KS password. It is optional.
          ├── -outFile                           # Certificate file to generate. It is optional. The file is output to the console if this parameter is not specified.

7.Sign a provisioning profile.

    sign-profile: Generate a provisioning profile signature.
          ├── -mode            # Signing mode, which can be localSign or remoteSign. It is mandatory.
          ├── -keyAlias        # Key alias. It is mandatory.
          ├── -keyPwd          # Key password. It is optional.
          ├── -profileCertFile # Profile signing certificate (certificate chain, represented by level 3 certificate, level 2 certificate, and root sequence). It is mandatory.
          ├── -inFile          # Raw provisioning profile. It is mandatory.
          ├── -signAlg         # Signature algorithm, which can be SHA256withECDSA or SHA384withECDSA. It is mandatory.
          ├── -keystoreFile    # KS file, in JKS or P12 format. It is mandatory if the signing mode is localSign.
          ├── -keystorePwd     # KS password. It is optional.
          ├── -outFile         # Signed provisioning profile to generate, in p7b format. It is mandatory.

8.Verify the provisioning profile signature.

     verify-profile: Verify the provisioning profile signature.
           ├── -inFile       # Signed provisioning profile, in p7b format. It is mandatory.
           ├── -outFil       # Verification result file (including the verification result and profile content), in json format. It is optional. The file is output to the console if this parameter is not specified.

9.Sign a HAP
  
     sign-app: Sign a HAP
          ├── -mode          # Signing mode, which can be localSign, remoteSign, or remoteResign. It is mandatory.
          ├── -keyAlias      # Key alias. It is mandatory.
          ├──-keyPwd         # Key password. It is optional.
          ├── -appCertFile   # Application signing certificate (certificate chain, represented by level 3 certificate, level 2 certificate, and root sequence). It is mandatory.
          ├── -profileFile   # Name of the signed provisioning profile. The profile is in p7b format if profileSigned is 1 and in json format if profileSigned is 0. It is mandatory.
          ├── -profileSigned # Specifies whether the profile is signed. The value 1 means signed, and value 0 means unsigned. The default value is 1. It is optional.
          ├── -inForm        # Raw file, in .zip (default) or .bin format. It is optional.
          ├── -inFile        # Raw application package, in zip or .bin format. It is mandatory.
          ├── -signAlg       # Signature algorithm, which can be SHA256withECDSA or SHA384withECDSA. It is mandatory.
          ├── -keystoreFile  # KS file, in JKS or P12 format. It is mandatory if the signing mode is localSign.
          ├── -keystorePwd   # KS password. It is optional.
          ├── -outFile       # Signed HAP file to generate. It is mandatory.

10.Verify the HAP Signature

      verify-app: verify the HAP signature.
         ├── -inFile          # Signed application file, in zip or bin format. It is mandatory.
         ├── -outCertchain    # Signed certificate chain file. It is mandatory.
         ├── -outProfile      # Profile of the application. It is mandatory.



  
#### Repositories Involved
   N/A
