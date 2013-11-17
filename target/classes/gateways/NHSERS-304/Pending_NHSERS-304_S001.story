Story: NHSERS-304 HL7 Messaging Gateway for PDS

Narrative:			
In order to service requests to present patient information to an end user
As an ERS gateway client 
I want to use the HL7 Messaging Gateway to retrieve Patient Demographic data
			
Scenario: NHSERS-304_S001 | PDS Query - Patient Data Retrieval when patient exists in PDS
Meta:
@author Ian Malone 
@Sprint 1
@testphase Core Regression 
@developedFromJIRA 304 26/09/13 PM
@lastReviewed 
@pending


Given a patient user has been imported into PDS via a JSON file <NhsId>.json
And the PDS Gateway is running
When the rest service <RestService> for this user is called with the <Method> method on port 8443
Then the HTTP result is <HTTPResult>
And the JSON result is <JSONResult>

Examples:
|NhsId     |Method|RestService                                |HTTPResult|JSONResult|
|1140000171|GET   |/ers-pds-gateway-service/person/@1140000171|200       |{"address":{"addr":[{"line":"The Laurels"},{"line":"The Drive"},{"line":"Bengeo"},{"line":"Hertford"},{"line":"Herts"}],"postalCode":"SG19 1TR","effectiveDateRange":{"start":"20080229","end":""}},"dateOfBirth":"1971-01-01","dateOfDeath":null,"gender":"1","name":{"familyName":"Wilde","givenName":[{"name":"MARTIN"},{"name":"MARTY"}],"prefix":"MR","effectiveDateRange":{"start":"","end":""}},"nhsNumber":"1140000171","preferredLanguage":"English","serialChangeNumber":12,"errorCodes":null,"errorDescriptions":null}|
|1130000091|GET   |/ers-pds-gateway-service/person/@1130000091|200       |{"address":{"addr":[{"line":"The Laurels"},{"line":"The Drive"},{"line":"Bengeo"},{"line":"Hertford"},{"line":"Herts"}],"postalCode":"SG19 1TR","effectiveDateRange":{"start":"20080229","end":""}},"dateOfBirth":"1971-01-01","dateOfDeath":null,"gender":"1","name":{"familyName":"Wilde","givenName":[{"name":"MARTIN"},{"name":"MARTY"}],"prefix":"MR","effectiveDateRange":{"start":"","end":""}},"nhsNumber":"1130000091","preferredLanguage":"English","serialChangeNumber":12,"errorCodes":null,"errorDescriptions":null}|
|2260000057|GET   |/ers-pds-gateway-service/person/@2260000057|200       |{"address":{"addr":[{"line":"The Laurels"},{"line":"The Drive"},{"line":"Bengeo"},{"line":"Hertford"},{"line":"Herts"}],"postalCode":"SG19 1TR","effectiveDateRange":{"start":"20080229","end":""}},"dateOfBirth":"1971-01-01","dateOfDeath":null,"gender":"1","name":{"familyName":"Wilde","givenName":[{"name":"MARTIN"},{"name":"MARTY"}],"prefix":"MR","effectiveDateRange":{"start":"","end":""}},"nhsNumber":"2260000057","preferredLanguage":"English","serialChangeNumber":12,"errorCodes":null,"errorDescriptions":null}|

