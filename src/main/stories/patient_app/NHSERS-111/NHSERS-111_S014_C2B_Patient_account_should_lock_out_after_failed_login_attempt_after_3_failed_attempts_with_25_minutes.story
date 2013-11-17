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

Scenario:	NHSERS-111_S014_C2B |Patient account should lock out after failed login attempt after 3 failed attempts with 25 minutes	
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins

GivenStories: patient_app/NHSERS-111/NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins.story
!-- Given a Patient User attempts to log in	
!-- And has 2x failed login attempts already within a 30 minute period
Given the failed login count for ubrn "<ubrn>" is 2
!-- When they submit their incorrect <login details> within the same 25 minutes on the login page
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
And I click the login button on the Login page
!-- Then the Patient User is unable to login	
And the Login Page will be shown
!-- And error message "Access to choose and book has been denied because of the 3 failed login attempts. Please contact the Choose and Book Appointments line on 
!-- the number listed below" displayed
Then the text "Access to choose and book has been denied because of the 3 failed login attempts. Please contact the Choose and Book Appointments line on the number listed below" should be displayed on the LoginPage
!-- And the failed login account is increased
And the failed login count for ubrn "<ubrn>" is 3
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
|pageName|pageTitle|ubrn|yearofbirth|password 				|SessionID		|Activity	|Date		|Time  		|Reason Code	|
|Login|e-Referral Service|1110 0000 0001|1971|Passwordxxx|SessionID x	|Login 		|Date x		|Time x		|Reason Code |
|Login|e-Referral Service|1130 0000 0011|1971|Passwordxxx|SessionID x	|Login 		|Date x		|Time x		|Reason Code |
|Login|e-Referral Service|1130 0000 0012|9999|Password01|SessionID x	|Login 		|Date x		|Time x		|Reason Code |
|Login|e-Referral Service|1130 0000 0013|9999|Password01|SessionID x	|Login 		|Date x		|Time x		|Reason Code |
