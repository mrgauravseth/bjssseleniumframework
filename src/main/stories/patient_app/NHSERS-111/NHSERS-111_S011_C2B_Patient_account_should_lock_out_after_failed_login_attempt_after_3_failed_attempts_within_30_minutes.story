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

Scenario:	NHSERS-111_S011_C2B |Patient account should lock out after failed login attempt after 3 failed attempts within 30 minutes
!--Pre-requisites
!--NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins

GivenStories: patient_app/NHSERS-111/NHSERS-111_S041_C2B_Patient_failed_login_attempts_increases_after_failed_logins.story
!-- Given a Patient User attempts to log in
!-- And has 2x failed login attempts already within a 30 minute period
Given the failed login count for ubrn "<ubrnno>" is 2
!-- When they submit their incorrect <login details> within the same 30 minutes on the login page
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
And I click the login button on the Login page
!-- Then the Patient User is unable to login	
Then the Login Page will be shown
!-- And "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these 		
!-- and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will  
!-- be able to help you" error message displayed 
!--And the text "Access to Choose and Book has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again" should be displayed on the Login page
!--And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page
And the text "Access to e-Referral Service has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again." should be displayed on the Login page
And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page	
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
!--And the failed login count for ubrn "<ubrnno>" is 3
And ubrn <ubrnno> is locked

!-- Unused example table
!--|Login|e-Referral Service|1110 0000 0001|1110 0000 0001|999|Password01|
!--|Login|e-Referral Service|1130 0000 0011|1110 0000 0001|2014|Password01|
!--|Login|e-Referral Service|1130 0000 0012|1110 0000 0001|19YY|Password01|
!--|Login|e-Referral Service|1130 0000 0013|YY85|Password01|
|-- |Login|e-Referral Service|1110 0000 0001|1971|Password01|

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0014|111000000014|1971|WrongPass|
