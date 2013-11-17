Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
@comment CV: is this test really needed given we have scenario S014?
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario: NHSERS-111_S012_C2B |Patient attempting to login after 3 failed attempts within 30 minutes failed login count is increased

!--Pre-requisites
!--NHSERS-111_S011_C2B_Patient_account_should_lock_out_after_failed_login_attempt_after_3_failed_attempts_within_30_minutes

GivenStories: patient_app/NHSERS-111/NHSERS-111_S011_C2B_Patient_account_should_lock_out_after_failed_login_attempt_after_3_failed_attempts_within_30_minutes.story
Given story NHSERS-111_S011_C2B is ran
When story NHSERS-111_S011_C2B executed successfully
!-- Given a user has entered incorrect details 3 times within 30 minutes
!-- When the failed login attempts count is viewed
!-- Then the failed login attempts count will be 3
!--Then the failed login count for ubrn "<ubrnno>" is 3
Then ubrn <ubrnno> is locked

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0022|111000000022|1971|WrongPass|

|--|Login|e-Referral Service|1130 0000 0011|
|--|Login|e-Referral Service|1130 0000 0012|
|--|Login|e-Referral Service|1130 0000 0013|
|-- |Login|e-Referral Service|1110 0000 0001|
