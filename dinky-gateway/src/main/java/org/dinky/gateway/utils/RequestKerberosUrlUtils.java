/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.gateway.utils;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestKerberosUrlUtils {
    public static Logger logger = LoggerFactory.getLogger(RequestKerberosUrlUtils.class);
    private String principal;
    private String keyTabLocation;

    public RequestKerberosUrlUtils() {}

    public RequestKerberosUrlUtils(String principal, String keyTabLocation) {
        this.principal = principal;
        this.keyTabLocation = keyTabLocation;
    }

    public RequestKerberosUrlUtils(String principal, String keyTabLocation, boolean isDebug) {
        this(principal, keyTabLocation);
        if (isDebug) {
            System.setProperty("sun.security.spnego.debug", "true");
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }

    public RequestKerberosUrlUtils(String principal, String keyTabLocation, String krb5Location, boolean isDebug) {
        this(principal, keyTabLocation, isDebug);
        //        System.setProperty("java.security.krb5.conf", krb5Location);
    }

    private static HttpClient buildSpengoHttpClient() {

        Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true))
                .build();

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {
            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        });

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
        return httpClient;
    }

    public HttpResponse callRestUrl(final String url, final String userId) {
        //        logger.warn(String.format("Calling KerberosHttpClient %s %s %s", this.principal, this.keyTabLocation,
        // url));
        Configuration config = new Configuration() {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                HashMap<String, Object> options = new HashMap<String, Object>() {
                    {
                        put("useTicketCache", "false");
                        put("useKeyTab", "true");
                        put("keyTab", keyTabLocation);
                        // Krb5 in GSS API needs to be refreshed so it does not throw the error
                        // Specified version of key is not available
                        put("refreshKrb5Config", "true");
                        put("principal", principal);
                        put("storeKey", "true");
                        put("doNotPrompt", "true");
                        put("isInitiator", "true");
                        put("debug", "true");
                    }
                };
                return new AppConfigurationEntry[] {
                    new AppConfigurationEntry(
                            "com.sun.security.auth.module.Krb5LoginModule",
                            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                            options)
                };
            }
        };
        Set<Principal> princ = new HashSet<Principal>(1);
        princ.add(new KerberosPrincipal(userId));
        Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
        try {
            // auth module：Krb5Login
            LoginContext lc = new LoginContext("Krb5Login", sub, null, config);
            lc.login();
            Subject serviceSubject = lc.getSubject();
            return Subject.doAs(serviceSubject, new PrivilegedAction<HttpResponse>() {
                HttpResponse httpResponse = null;

                @Override
                public HttpResponse run() {
                    try {
                        HttpClient spnegoHttpClient = buildSpengoHttpClient();
                        httpResponse = spnegoHttpClient.execute(new HttpGet(url));
                        return httpResponse;
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    return httpResponse;
                }
            });
        } catch (Exception le) {
            le.printStackTrace();
        }
        return null;
    }
}
