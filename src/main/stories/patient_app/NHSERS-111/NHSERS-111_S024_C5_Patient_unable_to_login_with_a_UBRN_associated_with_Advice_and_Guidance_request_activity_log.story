Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C5 
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S024_C5 | Patient unable to login with a UBRN associated with Advice and Guidance request - activity log
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S023_C5_Patient_unable_to_login_with_a_UBRN_associated_with_Advice_and_Guidance_request 

GivenStories: patient_app/NHSERS-111/NHSERS-111_S023_C5_Patient_unable_to_login_with_a_UBRN_associated_with_Advice_and_Guidance_request.story
!-- Given Patient has tried to login with valid login details and <UBRN> associated with Advice and Guidance request
!-- When they submit their login details on the login page
Given the text "You cannot make an appointment using this reference number. Please contact your GP practise for more information" should be displayed on the LoginPage
When I click the login button on the Login page	
!-- Then the  Patient User is unable to login
Then the Login Page will be shown
!-- And the Activity Log is updated with login 
!-- And Last Activity Date Time is updated
!-- And failed login attempt increases by 1
And the failed login count for ubrn "<ubrn>" is 1
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
|pageName|pageTitle|ubrn|yearofbirth|password||SessionID|Activity|Date|Time  		|Reason Code	|
|Login|e-Referral Service|1110 0000 0004|1974|Password04||SessionID x|LOGIN|Date x		|Time x		|Reason Code x  |

