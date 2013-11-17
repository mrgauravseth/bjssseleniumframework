
Scenario: NHSERS-304_S002 | PDS Query - Failed Patient Data Retrieval when patient requested does not exist in PDS
Meta:
@author Ian Malone 
@Sprint 1
@testphase Core Regression 
@developedFromJIRA 304 26/09/13 PM
@lastReviewed 
@pending

Given a patient user does not exist in PDS with a NHS Id of <NhsId>
And the PDS Gateway is running
When the rest service <RestService> for this user is called with the <Method> method on port 8443
Then the HTTP result is <HTTPResult>
And the JSON result is <JSONResult>

Examples:
|NhsId       |Method|RestService                                  |HTTPResult|JSONResult|
|123456789012|GET   |/ers-pds-gateway-service/person/@123456789012|404       ||

