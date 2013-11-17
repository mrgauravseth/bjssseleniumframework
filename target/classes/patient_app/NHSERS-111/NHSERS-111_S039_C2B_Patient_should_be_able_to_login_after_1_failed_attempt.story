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

Scenario:	NHSERS-111_S039_C2B | Patient should be able to login after 1 failed attempt
 
!--Pre-requisites
!--NHSERS-111_S007_C2B_Patient_should_be_unable_to_login_with_invalid_Year_of_birth
	
GivenStories: patient_app/NHSERS-111/NHSERS-111_S009_C2B_Patient_should_be_unable_to_login_with_invalid_password.story
!-- Given a Patient User has 1 failed login attempt
!-- When they submit their valid login details on the login page
Given the failed login count for ubrn "<ubrnno>" is 1
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<rightPass>" in the passwordField textbox on the Login page
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

!--unused examples table
!--|Login|e-Referral Service|1110 0000 0001|999|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!--|Login|e-Referral Service|1130 0000 0011|2014|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!--|Login|e-Referral Service|1130 0000 0012|19YY|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
!--|Login|e-Referral Service|1130 0000 0013|YY85|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|rightPass|
|Login|e-Referral Service|1110 0000 0017|111000000017|1971|wrongPass|Password01|

