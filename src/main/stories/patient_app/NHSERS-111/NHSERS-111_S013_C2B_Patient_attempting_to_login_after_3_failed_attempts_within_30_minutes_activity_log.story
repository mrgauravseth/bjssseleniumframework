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

Scenario: NHSERS-111_S013_C2B |Patient attempting to login after 3 failed attempts within 30 minutes - activity log
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S012_C2B_Patient_attempting_to_login_after_3_failed_attempts_within_30_minutes_failed_login_count_is_increased

GivenStories: patient_app/NHSERS-111/NHSERS-111_S012_C2B_Patient_attempting_to_login_after_3_failed_attempts_within_30_minutes_failed_login_count_is_increased.story
Given the failed login count for ubrn "<ubrn>" is 3
!-- Given a user has entered incorrect details 3 times
!-- When the user tries to login using correct details within 60 minutes of being locked out
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
And I click the login button on the Login page

!-- Then access to ERS is denied because of the 3 failed login attempts
Then the Login Page will be shown
 
!-- And error message "Access to choose and book has been denied because of the 3 failed login attempts. Please contact the Choose and Book Appointments line on 
!-- the number listed below" displayed
And the text "Access to choose and book has been denied because of the 3 failed login attempts. Please contact the Choose and Book Appointments line on the number listed below" should be displayed on the LoginPage
 
!-- And the Activity Log is updated with login attempt
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
|pageName|pageTitle|ubrn|yearofbirth|password  |SessionID		|Activity	|Date		|Time  		|Reason Code |	
|Login|e-Referral Service|1110 0000 0001|999					|Password01|SessionID x	|LOGIN 		|Date x		|Time x		|Reason Code x |
|Login|e-Referral Service|1130 0000 0011|2014				|Password01|SessionID x	|LOGIN 		|Date x		|Time x		|Reason Code x |
|Login|e-Referral Service|1130 0000 0012|19YY				|Password01|SessionID x	|LOGIN 		|Date x		|Time x		|Reason Code x |
|Login|e-Referral Service|1130 0000 0013|YY85				|Password01|SessionID x	|LOGIN 		|Date x		|Time x		|Reason Code x |
|-- |Login|e-Referral Service|1110 0000 0001|1971				|Password01|

