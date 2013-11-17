Story: NHSERS-111 Patient Login - Authentication
	
Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C4
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S022_C4 | Patient unable to login using an appointment booking in the past

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given the patient logs in with a <UBRN> associated with a <Booking in the past>
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their valid login details on the login page
And I click the login button on the Login page
!-- Then the Patient User is unable to login
Then the Login Page will be shown
!-- And Last Activity Date Time is updated 
!-- And error message "Appointment which your reference number is associated to takes place in the past. You can only log in with a reference number which has an	
!-- appointment in the future or is not yet booked. Please contact the choose and book appointments line for further information" displayed
And the text "The appointment which your reference number is associated to takes place in the past. You can only log in with a reference number which has an appointment in the future or is not yet booked. Please contact the e-Referral Service Appointments Line for further information." should be displayed on the Login page
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
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|Booking in the past	|
|Login|e-Referral Service|1110 0000 0003|111000000003|1973|Password03|5 Minutes after appointment start time|
