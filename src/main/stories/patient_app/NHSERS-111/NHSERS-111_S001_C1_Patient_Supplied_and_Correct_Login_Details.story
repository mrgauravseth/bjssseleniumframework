Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C1

Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S001_C1 | Patient Supplied and Correct Login Details	

GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story
!-- Given a patient user had a valid <UBRN> 	
!-- And a valid <year of birth>	
!-- And a valid <password> 
!-- And is not locked out		
!-- When they submit their login details on the login page	
!-- Then the patient user is able to successfully login to ERS displaying appropriate appointment request
Given Story Given Story NHSERS-111_S001_C00 is ran
When Story NHSERS-111_S001_C00 is executed successfully
!-- And failed login count is not increased
Then the failed login count for ubrn "<ubrn>" is 0
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

Examples:
|pageName|pageTitle|ubrn | yob	|password	|	
|Login|e-Referral Service|1110 0000 0001| 1971 |Password01|

