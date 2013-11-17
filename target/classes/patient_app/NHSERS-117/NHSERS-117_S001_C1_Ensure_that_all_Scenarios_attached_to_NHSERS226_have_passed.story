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

Scenario: NHSERS-117_S001_C1 | Ensure that all Scenarios attached to NHSERS-226 have passed

Meta:
@manual
@1acceptanceCriteria NHSERS-117_C1

@1Pre-requisites
@1NHSERS-226_NNN Patient Views “Booked” Referral

@1GivenStories: NHSERS-226_NNN

@1Given that the Manual Tester can view results for NHSERS-226
@1When the status of Scenarios for NHSERS-226 are all Passed
@1Then the Manual Tester is assured that Acceptance Criteria NHSERS-117_C1 has been satisfied

@skip