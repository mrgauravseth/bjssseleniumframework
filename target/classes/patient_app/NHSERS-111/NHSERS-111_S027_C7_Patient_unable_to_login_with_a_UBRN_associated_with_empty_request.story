Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C7	
@comment CV: currently this is not testable, it is not possible to have a ubrn without an appointment request in place, since ubrn is part of appointmentrequest entity		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S027_C7 | Patient unable to login with a UBRN associated with empty request	
Meta:
@pending

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User logins with a valid login details and <UBRN> associated with an empty request
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page
And I click the login button on the Login page
!-- Then the  Patient User is unable to login
Then the Login Page will be shown
!-- And error message "You cannot make an appointment using this reference number. Please contact your GP practise for more information" displayed
And the text "You cannot make an appointment using this reference number. Please contact your GP practice for more information." should be displayed on the Login page
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
|pageName|pageTitle|ubrn|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0006|1976|Password06|			
