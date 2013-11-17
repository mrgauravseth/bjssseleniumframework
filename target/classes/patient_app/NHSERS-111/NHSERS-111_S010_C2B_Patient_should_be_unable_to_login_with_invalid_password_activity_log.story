Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@aprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S010_C2B | Patient should be unable to login with invalid password- activity log
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S009_C2B_Patient_should_be_unable_to_login_with_invalid_password

GivenStories: patient_app/NHSERS-111/NHSERS-111_S009_C2B_Patient_should_be_unable_to_login_with_invalid_password.story
Given the failed login count for ubrn "<ubrn>" is 1
!-- Given Patient has supplied invalid password
!-- When I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page
!-- And I click the login button on the Login page

Then the Activity Log is updated with failed login attempt
!-- And <SessionID> is logged
!-- And Activity <Activity> is logged 
!-- And Date <Date> is logged
!-- And Time <Time> is logged
!-- And UBRN <UBRN> is logged
!-- And Reason Code <Reason Code> is logged
!-- Needs clarification from Dev/BAs as to what will be logged
!-- Format of Activity Log is YYYY-MM-DD HH:mm:ss,sss [ACTIVITY] SessionID =  XXXXXXXXXX  UBRN =  XXXXXXXXXXXXX  Reason Code =  xxxxxxxxxxxx 
!-- <SessionID> = XXXXXXXXXXX
!-- <Activity> is LOGIN
!-- <Date> format is YYYY-MM-DD
!-- <Time> format is HH:mm:ss,sss 

Examples:
|pageName|pageTitle|ubrn          |yearofbirth	|password |SessionID		|Activity	|Date		|Time  		|Reason Code	|  
|Login|e-Referral Service|1130 0000 0014|1971	|password x|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 0015|1971	|qwertyuiopasdfghjklmnbvcxzqwertyuiopasdfg|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 0016|1971	|qwert|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |

|Login|e-Referral Service|1130 0000 xxxx|1971	|qwerty|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 xxxx|1971	|13245|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 xxxx|1971	|13246578901234567890132465789012345678901|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 xxxx|1971	|1a2s3|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 xxxx|1971	|qwerty space|SessionID x	|Login 		|Date x		|Time x		|Reason Code x  |
