
Scenario: NHSERS-1335_S002 | Failed retrieval of function codes for a non existing NHS person
Meta:
@author Ian Malone 
@Sprint 1
@testphase Core Regression 
@developedFromJIRA 1335 01/10/13 PM
@lastReviewed 
@pending


Given the SDS API service is running
And NHS Person <Uid> does not exist in the SDS
When the rest service <RestService> for this user is called with the <Method> method on port 8443
Then the HTTP result is <HTTPResult>
And the JSON result is <JSONResult>

Examples:
Uid   |Method|RestService                                               |HTTPResult|JSONResult|
|91002|GET   |/ers-sds-gateway/nhsOrgPerson/@91002/businessFunctionCodes|404||