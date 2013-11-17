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

Scenario:	NHSERS-111_S020_C3 | patient is Currently Locked out

!--Pre-requisites
!--NHSERS-111_S011_C2B_Patient_account_should_lock_out_after_failed_login_attempt_after_3_failed_attempts_within_30_minutes

GivenStories: patient_app/NHSERS-111/NHSERS-111_S011_C2B_Patient_account_should_lock_out_after_failed_login_attempt_after_3_failed_attempts_within_30_minutes.story
!--- Given a Patient User attempts login with <UBRN> which have been used before >3x incorrectly within 30 minutes within the last 60 minutes
Given I am on the login page
!-- When they submit their valid login details on the login page	
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
And I click the login button on the Login page
!-- Then the  Patient User is unable to login	
!-- And the Login Page will be redisplayed
Then the Login Page will be shown
!-- And the Activity Log is updated with login attempt
!-- And Last Activity Date Time is updated 
!-- And "Access to choose and book has been denied because of the 3 failed log in attempts. Please contact the Choose and Book Appointments line on the 		
!-- number listed below" message displayed
And the text "Access to e-Referral Service has been denied because of 3 failed log in attempts. Please contact the e-Referral Service Appointments Line on the number listed below." should be displayed on the Login page

!-- unused table data
!-- |Login|e-Referral Service|1110 0000 0001|999|Password01|
!-- |Login|e-Referral Service|1130 0000 0011|2014|Password01|
!-- |Login|e-Referral Service|1130 0000 0012|19YY|Password01|
!-- |Login|e-Referral Service|1130 0000 0013|YY85|Password01|
!-- |Login|e-Referral Service|1110 0000 0001|1971|Password01|

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0015|111000000015|1971|WrongPass|