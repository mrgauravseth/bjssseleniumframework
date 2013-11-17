Story: NHSERS-111 Patient Login - Authentication
		
Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2Bd

Narrative:	
In order to manage my Appointment Request(s)
As a Patient User 
I want to log into the e-RS 
	
Scenario:	NHSERS-111_S008_C2B | Patient should be unable to login with invalid Year of birth- activity log
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S007_C2B_Patient_should_be_unable_to_login_with_invalid_Year_of_birth
	
GivenStories: patient_app/NHSERS-111/NHSERS-111_S007_C2B_Patient_should_be_unable_to_login_with_invalid_Year_of_birth.story
Given the failed login count for ubrn "<ubrn>" is 1
!-- Given Patient has supplied invalid year of birth
!-- When they submit their login details on the login page
!-- And I click the login button on the Login page

!-- Then the Activity Log is updated with failed login attempt
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
|pageName|pageTitle|ubrn|yearofbirth|password|SessionID|Activity|Date|Time||Reason Code|
|Login|e-Referral Service|1110 0000 0001|999|Password01|SessionID x	|LOGIN		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 0011|2014|Password01|SessionID x	|LOGIN		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 0012|19YY|Password01|SessionID x	|LOGIN		|Date x		|Time x		|Reason Code x  |
|Login|e-Referral Service|1130 0000 0013|YY85|Password01|SessionID x	|LOGIN		|Date x		|Time x		|Reason Code x  |
