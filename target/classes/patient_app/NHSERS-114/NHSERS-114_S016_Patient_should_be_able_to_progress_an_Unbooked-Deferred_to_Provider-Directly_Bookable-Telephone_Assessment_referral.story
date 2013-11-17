Story: NHSERS-114 Placeholder  

Meta:
@author Scott Redden
@sprint 1
@testphase Core Regression 
@placeholder


Narrative:	
In order to ensure dependancies are maintained
As a Automation tester 
I want to create this place holder

Scenario: NHSERS-114_S016 | Patient should be able to progress an Unbooked (Deferred to Provider), Directly Bookable, Telephone Assessment referral
Meta:
@skip
!-- Pre-requisites
!-- NHSERS-111_S001 PatientLoginSuccess
!-- NHSERS-114_S016 Patient should be able to progress an Unbooked (Deferred to Provider), Directly Bookable, Telephone Assessment referral
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story
!-- Given that the Patient is logged in to eRS
Given I am on the login page

