package io.consentric.examples;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.UUID;

public class Main {

    /*
     * Provide your client ID and secret here.
     *
     * Note: it is very bad practise to hardcode these into your applications as done in this simple example
     * Doing so risks exposure of these values to the outside world. These values should be stored outside
     * your application in a secure manner, with controlled access.
     */
    private static final String CLIENT_ID = "<YOUR CLIENT ID>";
    private static final String CLIENT_SECRET = "<YOUR CLIENT SECRET>";
    private static final String API_AUDIENCE = "https://api.consentric.io";

    /*
     * Provide you application ID and IDs for other Consentric entities here
     */
    private static final String BASE_URL = "https://api.consentric.io";
    private static final String APPLICATION_ID = "<YOUR APPLICATION ID>";
    private static final String PRIVACY_POLICY_ID = "<YOUR PRIVACY POLICY ID>";
    private static final String OPTION_ID = "<YOUR OPTION ID>";
    private static final String PERMISSION_STATEMENT_ID = "<YOUR PERMISSION STATEMENT ID>";

    public static void main(String[] args) {

        /*
         * Retrieve a JWT for your client
         */
        HttpResponse<JsonNode> httpResponse = Unirest.post("https://consentric.eu.auth0.com/oauth/token")
                .header("content-type", "application/json")
                .body("{\"client_id\":\"" + CLIENT_ID + "\",\"client_secret\":\"" + CLIENT_SECRET + "\",\"audience\":\"" + API_AUDIENCE + "\",\"grant_type\":\"client_credentials\"}")
                .asJson();

        if (httpResponse.getStatus() != 200) {
            System.out.printf("Error obtaining JWT: %s\n", httpResponse.getBody());
            System.exit(1);
        }

        String access_token = (String) httpResponse.getBody().getObject().get("access_token");

        System.out.printf("Retrieved JWT\n");


        /*
         * Construct the JSON for citizen creation and POST to the /v1/citizens endpoint to create a citizen
         */

        String citizenReference = UUID.randomUUID().toString();  // Citizen references should be unique strings

        String citizen = "{\n" +
                "  \"applicationId\": \"" + APPLICATION_ID + "\",\n" +
                "  \"externalRef\": \"" + citizenReference + "\",\n" +
                "  \"givenName\": \"Test\",\n" +
                "  \"familyName\": \"Citizen2\",\n" +
                "  \"address\": {\n" +
                "    \"streetAddress\": \"23 Acacia Avenue\",\n" +
                "    \"addressLocality\": \"Trust Town\",\n" +
                "    \"addressRegion\": \"Gloucestershire\",\n" +
                "    \"addressCountry\": \"UK\",\n" +
                "    \"postalCode\": \"TT1 1AS\"\n" +
                "  }\n" +
                "}";

        HttpResponse<JsonNode> createCitizenResponse = Unirest.post(BASE_URL + "/v1/citizens")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + access_token)
                .body(citizen)
                .asJson();

        System.out.printf("Citizen created: %s\n\n", createCitizenResponse.getBody().toPrettyString());


        /*
         * Construct and create a transaction for the created citizen
         */

        String citizenTransactions = "{\n" +
                "  \"applicationId\": \"" + APPLICATION_ID + "\",\n" +
                "  \"externalRef\": \"" + citizenReference + "\",\n" +
                "  \"changes\": [\n" +
                "    {\n" +
                "      \"optionType\": \"option\",\n" +
                "      \"optionId\": \"" + OPTION_ID + "\",\n" +
                "      \"justification\": \"consent\",\n" +
                "      \"state\": \"GRANTED\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"privacyPolicyId\": \"" + PRIVACY_POLICY_ID + "\",\n" +
                "  \"permissionStatementId\": \"" + PERMISSION_STATEMENT_ID + "\"\n" +
                "}";


        HttpResponse<JsonNode> transactionPostResponse = Unirest.post(BASE_URL + "/v1/permissions/transactions")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + access_token)
                .body(citizenTransactions)
                .asJson();

        if (transactionPostResponse.getStatus() != 200) {
            System.out.printf("Error creating transaction: %s\n", transactionPostResponse.getBody());
            System.exit(1);
        }

        System.out.printf("transaction POST response: %s\n\n", transactionPostResponse.getBody().toPrettyString());

        try {
            Thread.sleep(1000); // Allow consentric to process the transaction
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
         * Get a the citizens permissions out of Consentric
         */
        HttpResponse<JsonNode> permissionsResponse = Unirest.get(BASE_URL + "/v1/permissions")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + access_token)
                .queryString("applicationId", APPLICATION_ID)
                .queryString("externalRef", citizenReference)
                .asJson();

        if (permissionsResponse.getStatus() != 200) {
            System.out.printf("Error getting citizen permissions: %s\n", permissionsResponse.getBody());
            System.exit(1);
        }

        System.out.printf("Retrieved permissions: %s\n\n", permissionsResponse.getBody().toPrettyString());
    }
}
