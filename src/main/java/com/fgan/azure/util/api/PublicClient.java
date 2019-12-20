//package com.fgan.azure.util.api;
//
//import com.microsoft.aad.adal4j.AuthenticationContext;
//import com.microsoft.aad.adal4j.AuthenticationResult;
//
//import javax.naming.ServiceUnavailableException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//public class PublicClient {
//
//    public static void main(String args[]) throws Exception {
//
//    }
//
//    private static AuthenticationResult getAccessTokenFromUserCredentials(
//            String username, String password) throws Exception {
//        AuthenticationContext context = null;
//        AuthenticationResult result = null;
//        ExecutorService service = null;
//        try {
//            service = Executors.newFixedThreadPool(1);
//            context = new AuthenticationContext(AUTHORITY, false, service);
//            Future<AuthenticationResult> future = context.acquireToken(
//                    RESOURCE, CLIENT_ID, username, password, null);
//            result = future.get();
//        } finally {
//            service.shutdown();
//        }
//
//        if (result == null) {
//            throw new ServiceUnavailableException(
//                    "authentication result was null");
//        }
//        return result;
//    }
//}
