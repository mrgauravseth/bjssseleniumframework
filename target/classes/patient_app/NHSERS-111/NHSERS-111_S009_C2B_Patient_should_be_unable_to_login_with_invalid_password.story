Story: NHSERS-111 Patient Login - Authentication
		
Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
@comments there is a bug stopping passwords incrementing login count if they are <6 or > 10 - jira bug 1957

Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S009_C2B | Patient should be unable to login with invalid password

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User has a valid <UBRN>
When I enter "<ubrn>" in the ubrn textboxes on the Login page	
!-- And a <Year of Birth>
And I enter "<yearofbirth>" in the yobField textbox on the Login page	
!-- And an invalid <Password>	
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page
And I click the login button on the Login page		
!-- Then the  Patient User is unable to login	
!-- And the Login Page will be shown
Then the Login Page will be shown
!-- And "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these 		
!-- and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will  
!-- be able to help you" error message displayed 
!--And the text "Access to Choose and Book has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again" should be displayed on the Login page
!--And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page
And the text "Access to e-Referral Service has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again." should be displayed on the Login page
And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page	
!-- And failed login attempts increases
And the failed login count for ubrn "<ubrnno>" is 1

!-- unused example table
!--|Login|e-Referral Service|1130 0000 0016|1971|qwert|
!--|Login|e-Referral Service|1130 0000 xxxx|1971|13245|
!--|Login|e-Referral Service|1130 0000 xxxx|1971|1a2s3|
!--|Login|e-Referral Service|1110 0000 0009|111000000009|1971|qwertyuiopasdfghjklmnbvcxzqwertyuiopasdfg|
!--|Login|e-Referral Service|1110 0000 0011|111000000011|1971|13246578901234567890132465789012345678901|
	
Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0008|111000000008|1971|password x|
|Login|e-Referral Service|1110 0000 0010|111000000010|1971|qwerty|
|Login|e-Referral Service|1110 0000 0012|111000000012|1971|a space|