Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2Bd	
@pending
@comment this can't run as if the urbn is invalid then it can't log an error against it?
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 
	
Scenario:	NHSERS-111_S006_C2B | Patient unable login with invalid UBRN - activity log updated 

!--Pre-requisites
!--NHSERS-111_S005_C2B_Patient_unable_login_with_invalid_details

GivenStories: patient_app/NHSERS-111/NHSERS-111_S005_C2B_Patient_unable_login_with_invalid_UBRN.story

!-- Given Patient user has supplied incorrect UBRN 
!-- When the patient is presented with error message
Given Story NHSERS-111_S005_C2B is ran
When Story NHSERS-111_S005_C2B is executed successfully
!-- Then the Activity log is updated with failed login attempt
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
|pageName|pageTitle|SessionID	    |Activity	|Date		|Time  		|UBRN	 | UBRN2  | UBRN3	|Reason Code   |	
|Login|e-Referral Service|SessionID x	|LOGIN	 	|Date x		|Time x		|UBRNx	 | UBRNx  | UBRNx	|Reason Code x |
		
