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
package org.geoint.canon.server.security.event;

import java.time.Instant;
import java.util.Arrays;
import javax.security.cert.X509Certificate;
import org.geoint.canon.server.security.ClientCertificateUtil;

/**
 * This is a "raw" event, capturing the client request to register an X509
 * certificate with Canon.
 * <p>
 * Prior to publication this event has been minimally validated, and any handler
 * consuming this request should understand that this contains raw request data
 * from the client and has not been validated with the domain.
 *
 *
 */
public class RegisterClientCertRequest {

    private final String requestedProfileId;
    private final String subjectName;
    private final String serialNum;
    private final Instant validFrom;
    private final Instant validUntil;
    private final byte[] sha1Fingerprint;
    private final byte[] sha256Fingerprint;

    public RegisterClientCertRequest(String requestedProfileId,
            String subjectName, String serialNum,
            Instant validFrom, Instant validUntil,
            byte[] sha1Fingerprint,
            byte[] sha256Fingerprint) {
        this.requestedProfileId = requestedProfileId;
        this.subjectName = subjectName;
        this.serialNum = serialNum;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.sha1Fingerprint = sha1Fingerprint;
        this.sha256Fingerprint = sha256Fingerprint;
    }

    /**
     * Create a new request event.
     *
     * @param requestedProfileId client requested profile Id (may be null)
     * @param cert client certificate
     * @return register request
     */
    public static RegisterClientCertRequest newRequest(
            String requestedProfileId,
            X509Certificate cert) {

        return new RegisterClientCertRequest(requestedProfileId,
                cert.getSubjectDN().getName(),
                cert.getSerialNumber().toString(),
                cert.getNotBefore().toInstant(),
                cert.getNotAfter().toInstant(),
                ClientCertificateUtil.sha1Fingerprint(cert),
                ClientCertificateUtil.sha256Fingerprint(cert));
    }

    /**
     * Client certificate serial number.
     *
     * @return certificate serial number
     */
    public String getSerialNum() {
        return serialNum;
    }

    /**
     * Client-requested profile identifier to link the client certificate.
     *
     * @return client-provided profile id or null if client did not provide
     */
    public String getRequestedProfileId() {
        return requestedProfileId;
    }

    /**
     * Client certificate subject name.
     *
     * @return cetificate subject name
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Start of client certificate validity period.
     *
     * @return start of client validity period
     */
    public Instant getValidFrom() {
        return validFrom;
    }

    /**
     * End of client certificate validity period.
     *
     * @return end of client validity period
     */
    public Instant getValidUntil() {
        return validUntil;
    }

    /**
     * SHA1 fingerprint of the client certificate.
     *
     * @return sha1 fingerprint
     */
    public byte[] getSha1Fingerprint() {
        return sha1Fingerprint;
    }

    /**
     * SHA256 fingerprint of the client certificate.
     *
     * @return sha256 fingerprint
     */
    public byte[] getSha256Fingerprint() {
        return sha256Fingerprint;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.sha1Fingerprint);
        hash = 97 * hash + Arrays.hashCode(this.sha256Fingerprint);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegisterClientCertRequest other = (RegisterClientCertRequest) obj;
        if (!Arrays.equals(this.sha1Fingerprint, other.sha1Fingerprint)) {
            return false;
        }
        if (!Arrays.equals(this.sha256Fingerprint, other.sha256Fingerprint)) {
            return false;
        }
        return true;
    }

}
