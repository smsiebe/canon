/*
 * Copyright 2016 geoint.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.canon.server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class ClientCertificateUtil {

    private static final String CERT_PARAM_NAME
            = "javax.servlet.request.X509Certificate";

    //JRE specification requires every java platform to support these
    enum StandardDigestAlgorithm {
        SHA256("SHA-256"),
        SHA1("SHA-1");
        private final String algorithmName;

        StandardDigestAlgorithm(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

    }

    /**
     * Return an array of client certificates for a servlet request, or null.
     *
     * @param request servlet request
     * @return client certificates or null
     */
    public static X509Certificate[] getClientCertificates(
            HttpServletRequest request) {
        Object certObject = request.getAttribute(CERT_PARAM_NAME);

        if (certObject == null
                || !certObject.getClass().isArray()
                || !X509Certificate.class
                .equals(certObject.getClass().getComponentType())) {
            return null;
        }
        return (X509Certificate[]) request.getAttribute(CERT_PARAM_NAME);
    }

    /**
     * Generate a SHA-1 fingerprint for the certificate.
     *
     * @param cert
     * @return
     * @throws CertificateEncodingException
     */
    public static byte[] sha1Fingerprint(X509Certificate cert)
            throws CertificateEncodingException {
        return generateFingerprint(cert, StandardDigestAlgorithm.SHA1);
    }

    /**
     * Generate a SHA-256 fingerprint for the certificate.
     *
     * @param cert
     * @return
     * @throws CertificateEncodingException
     */
    public static byte[] sha256Fingerprint(X509Certificate cert)
            throws CertificateEncodingException {
        return generateFingerprint(cert, StandardDigestAlgorithm.SHA256);
    }

    private static byte[] generateFingerprint(X509Certificate cert, StandardDigestAlgorithm algo)
            throws CertificateEncodingException {
        try {
            MessageDigest d = MessageDigest.getInstance(algo.getAlgorithmName());
            d.update(cert.getEncoded());
            return d.digest();
        } catch (NoSuchAlgorithmException ex) {
            //won't get here unless the JRE is invalid
            throw new RuntimeException("Missing JRE-required algorithm.");
        }
    }
}
