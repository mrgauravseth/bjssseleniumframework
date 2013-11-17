Story: NHSERS-117 Patient Views Referral  

Meta:
@author Gaurav Seth
@sprint 1
@testphase Core Regression 
@lastReviewed
@inUse True
@acceptanceCriteria c1xxx
@manualonly

Narrative:	
In order to check my referral
As a Patient User 
I want to view my referral in e-RS 

Scenario: NHSERS-117_S002_C2 | Ensure that all Scenarios attached to NHSERS-113 have passed

Meta:
@manual
@1acceptanceCriteria NHSERS-117_C1
@1! Pre-requisites
@1! NHSERS-113_NNN Patient Views Unbooked Referral

@1! GivenStories: NHSERS-113_NNN

@1Given that the Manual Tester can view results for NHSERS-113
@1When the status of Scenarios for NHSERS-113 are all Passed
@1Then the Manual Tester is assured that Acceptance Criteria NHSERS-117_C2 has been satisfied

@skip