Scenario: NHSERS-304_S003 | PDS Query - Failed retrievals due to incorrect API requests
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
|--Incorrect URLs
|2260000064|GET   |/persons/@2260000064                       |404       ||
|2260000064|GET   |/persons                                   |404       ||
|--Incorrect request methods
|2260000064|POST  |/ers-pds-gateway-service/person/@2260000064|405       ||
|2260000064|PUT   |/ers-pds-gateway-service/person/@2260000064|405       ||
|2260000064|DELETE|/ers-pds-gateway-service/person/@2260000064|405       ||
|-- 2260000064|INVALID|/ers-pds-gateway-service/person/@2260000064|501||
