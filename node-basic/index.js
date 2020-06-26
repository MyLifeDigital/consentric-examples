// Import required modules
var AuthenticationClient = require('auth0').AuthenticationClient;
var axios = require('axios');

// Define Auth0 credentials here
var clientId = ""
var clientSecret = ""
var apiAudience = "https://api.consentric.io"

// Define Consentric Options
var apiHost = "api.consentric.io"
var applicationId = ""
var externalRef = ""
var privacyPolicyId = ""
var permissionStatementId = ""
var optionId = ""

// Configure a new Auth0 Authentication Client
var auth0 = new AuthenticationClient({
  domain: 'consentric.eu.auth0.com',
  clientId: clientId,
  clientSecret: clientSecret,
});

// Request an Auth0 JWT
auth0.clientCredentialsGrant({
    audience: apiAudience
  },
  function(error, response) {
    if (error) throw new Error(error)

    // Create a Citizen
    axios({
      method: 'POST',
      url: `https://${apiHost}/v1/citizens`,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${response.access_token}`
      },
      data: {
        applicationId,
        externalRef,
        givenName: "Test",
        familyName: "Citizen",
        address: {
          streetAddress: "23 Acacia Avenue",
          addressLocality: "Trust Town",
          addressRegion: "Gloucestershire",
          addressCountry: "UK",
          postalCode: "TT1 1AS"
        }
      }
    })
    .then(data => {
      if (data.status != 200) throw new Error("Non-200 Response Code Detected")

      console.log("Citizen Created!")
      console.dir(data.data)

      // Create a Transaction
      axios({
        method: 'POST',
        url: `https://${apiHost}/v1/permissions/transactions`,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${response.access_token}`
        },
        data: {
          applicationId,
          externalRef,
          changes: {
            optionType: "option",
            optionId,
            justification: "consent",
            state: "GRANTED"
          },
          privacyPolicyId,
          permissionStatementId,
        }
      })
      .then(data => {
        if (data.status != 200) throw new Error("Non-200 Response Code Detected")

        console.log("Citizen Transaction Created!")
        console.dir(data.data)

        // Get Citizen Permissions
        axios({
          method: 'GET',
          url: `https://${apiHost}/v1/permissions?applicationId=${applicationId}&externalRef=${externalRef}`,
          headers: {
            Authorization: `Bearer ${response.access_token}`
          }
        })
        .then(data => {
          if (data.status != 200) throw new Error("Status Code 200 was not found.")

          console.log("Citizen Permissions:")
          console.dir(data.data)
        })
        .catch(error => {
          console.log("Error getting permissions")
          throw new Error(error)
        })
      })
      .catch(error => {
        console.log("Error creating transaction")
        throw new Error(error)
      })
    })
    .catch(error => {
      console.log("Error creating Citizen")
      throw new Error(error)
    })
  }
)