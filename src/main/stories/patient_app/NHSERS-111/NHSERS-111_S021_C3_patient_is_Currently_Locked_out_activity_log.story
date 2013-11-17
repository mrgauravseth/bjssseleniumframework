Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C3	
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S021_C3 | patient is Currently Locked out - activity log
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S020_C3_patient_is_Currently_Locked_out


GivenStories: patient_app/NHSERS-111/NHSERS-111_S020_C3_patient_is_Currently_Locked out.story

!-- Given patient user is locked out
!-- When they try to login
Given the text "Access to choose and book has been denied because of the 3 failed log in attempts. Please contact the Choose and Book Appointments line on the number listed below" should be displayed on the LoginPage
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
And I click the login button on the Login page
!-- Then the Patient user is unable to login
Then the Login Page will be shown
!-- And the Activity Log is updated with failed login attempt
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
|pageName|pageTitle|ubrn	       |yearofbirth			|password  |SessionID		|Activity	|Date		|Time  			|Reason Code	|
|Login|e-Referral Service|1110 0000 0001|999					|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
|Login|e-Referral Service|1130 0000 0011|2014				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
|Login|e-Referral Service|1130 0000 0012|19YY				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
|Login|e-Referral Service|1130 0000 0013|YY85				|Password01|SessionIDx|ActivityX|DateX|TimeX|ReasonCodeX|
