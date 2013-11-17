Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S040_C2B | Patient should be able to login after 2 failed attempt

!--Pre-requisites
!--NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins

GivenStories: patient_app/NHSERS-111/NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins.story
!-- Given a Patient User attempts to log in	
!-- And has 2x failed login attempts already
Given the failed login count for ubrn "<ubrnno>" is 2
!-- When they submit their correct <login details> on the login page
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<rightPassword>" in the passwordField textbox on the Login page
And I click the login button on the Login page	
Then the patient user is able to successfully login to ERS	
!-- And the Activity Log is updated with login 
!-- And Last Activity Date Time is updated
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

!-- unused example table
!-- |Login|e-Referral Service|1110 0000 0001|999|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!-- |Login|e-Referral Service|1130 0000 0011|2014|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!-- |Login|e-Referral Service|1130 0000 0012|19YY|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!-- |Login|e-Referral Service|1130 0000 0013|YY85|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|rightPassword|
|Login|e-Referral Service|1110 0000 0018|111000000018|1971|wrongPass|Password01|


