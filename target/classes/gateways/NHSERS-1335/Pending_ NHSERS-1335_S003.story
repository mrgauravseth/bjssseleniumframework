
Scenario: NHSERS-1335_S003 | Failed retrieval of function codes for an NHS person with no Business Functions
Meta:
@author Ian Malone 
@Sprint 1
@testphase Core Regression 
@developedFromJIRA 1335 01/10/13 PM
@lastReviewed 
@pending 


Given the SDS API service is running
And NHS Person <Uid> has no Business Function Codes in the SDS
!-- When the rest service <RestService> is called with the <Method> method. 
When the rest service <RestService> for this user is called with the <Method> method on port 8443
Then the HTTP result is <HTTPResult>
And the JSON result excluding current time is <JSONResult>

Examples:
Uid   |Method|RestService|HTTPResult|JSONResult|
|103260396988|GET|/ers-sds-gateway/nhsOrgPerson/@103260396988/businessFunctions|200|{"List":{"source":{"type":"Professional","reference":"/nhsOrgPerson/@103260396988/businessFunctions"},"date":"2013-11-04T11:16:34Z","ordered":true,"mode":"readonly","entry":[]}}|