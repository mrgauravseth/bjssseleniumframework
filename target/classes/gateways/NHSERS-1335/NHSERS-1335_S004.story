
Scenario: NHSERS-1335_S004 | Failed retrieval of function codes when REST Api is incorrect
Meta:
@author Ian Malone 
@Sprint 1
@testphase Core Regression 
@developedFromJIRA 1335 01/10/13 PM
@lastReviewed 
@skip

Given the SDS API service is running
!-- And NHS Person <Uid> has associated Business Function Codes <FunctionCodes>
When the rest service <RestService> for this user is called with the <Method> method on port 8080
Then the HTTP result is <HTTPResult>
And the JSON result is <JSONResult>

Examples:
Uid   |FunctionCodes                                                  |Method|RestService                                       |HTTPResult|JSONResult|
|--Incorrect URL
|11001|"B0420","B0470","B0992","B1615","B0382","B0330","B0312","B0620"|GET|/ers-sds-gateway/nhsOrgPerson/@11001/businessFunction|404||


|--Incorrect request methods
|11001|"B0420","B0470","B0992","B1615","B0382","B0330","B0312","B0620"|PUT|/ers-sds-gateway/nhsOrgPerson/@11001/businessFunctions|405||
|11001|"B0420","B0470","B0992","B1615","B0382","B0330","B0312","B0620"|POST|/ers-sds-gateway/nhsOrgPerson/@11001/businessFunctions|405||
|11001|"B0420","B0470","B0992","B1615","B0382","B0330","B0312","B0620"|DELETE|/ers-sds-gateway/nhsOrgPerson/@11001/businessFunctions|405||
|-- 11001|"B0420","B0470","B0992","B1615","B0382","B0330","B0312","B0620"|INVALID|/ers-sds-gateway/nhsOrgPerson/@11001/businessFunctions|501||
