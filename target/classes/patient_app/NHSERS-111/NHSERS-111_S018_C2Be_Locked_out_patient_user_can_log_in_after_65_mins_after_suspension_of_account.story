Story: NHSERS-111 Patient Login - Authentication
	
Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2Be
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario NHSERS-111_S018_C2Be | Locked out patient user can log in after 65 mins after suspension of account

!--Pre-requisites
!--NHSERS-111_S012_C2B_Patient_attempting_to_login_after_3_failed_attempts_within_30_minutes_failed_login_count_is_increased

GivenStories: patient_app/NHSERS-111/NHSERS-111_S012_C2B_Patient_attempting_to_login_after_3_failed_attempts_within_30_minutes_failed_login_count_is_increased.story

!-- Given user has 3 failed login attempts within 30 minutes and their access has been suspended
Given I am on the login page
!-- When they wait 65 mins and submit their correct login details
!--When the user waits 65 minutes
When login was locked for ubrn <ubrnno>, 65 minutes ago
And I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<rightPassword>" in the passwordField textbox on the Login page
And I click the login button on the Login page
!-- Then they will be able to login
Then the patient user is able to successfully login to ERS
!-- And the Activity Log is updated with login 
!-- And the failed login account is now zero
And the failed login count for ubrn "<ubrn>" is 0
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

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|rightPassword|
|Login|e-Referral Service|1110 0000 0021|111000000021|1971|wrongPass|Password01|
|--|Login|e-Referral Service|1130 0000 0011|2014				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
|--|Login|e-Referral Service|1130 0000 0012|19YY				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
|--|Login|e-Referral Service|1130 0000 0013|YY85				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
