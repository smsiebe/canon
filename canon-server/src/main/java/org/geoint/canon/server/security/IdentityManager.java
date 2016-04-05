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

import java.util.Optional;
import javax.security.cert.X509Certificate;

/**
 *
 */
public interface IdentityManager {

    /**
     * Returns a profile associated with the certificate, if one exists.
     *
     * @param cert client certificate
     * @return linked profile or null
     */
    Optional<AuthenticatedProfile> findProfile(X509Certificate... cert);

//        Optional<AuthenticatedProfile> profile = Arrays.stream(certs)
//                .map(identity::findProfile)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .findFirst();
    /**
     * Returns the profile for the unique identifier, or null if the profile is
     * not known to the identity manager.
     *
     * @param profileId unique profile identifier
     * @return profile or null
     */
    Optional<AuthenticatedProfile> findProfile(String profileId);

    /**
     * Returns the profile by its unique id or throws an exception.
     *
     * @param profileId unique profile identifier
     * @return profile
     * @throws UnknownIdentityException if the profileId is not known to the
     * IdentityManager
     */
    AuthenticatedProfile getProfile(String profileId)
            throws UnknownIdentityException;

}
