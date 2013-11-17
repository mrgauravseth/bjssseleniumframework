Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
@comment CV: removed some examples which are no longer possible
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S005_C2B | Patient unable login with invalid UBRN

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User has an invalid <UBRN>	
When I enter "<ubrn>" in the ubrn textboxes on the Login page
!-- And a valid <Year of Birth>
And I enter "<yearofbirth>" in the yobField textbox on the Login page	
!-- And a valid <Password>
And I enter "<password>" in the passwordField textbox on the Login page	
!-- When they submit their login details on the login page	
And I click the login button on the Login page	
!-- Then the  Patient User is unable to login	
!--And the Login Page will be shown
!-- And "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these 		
!-- and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will  
!-- be able to help you" error message displayed
!--Then the text "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you" should be displayed on the Login page
Then the text "Access to e-Referral Service has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again." should be displayed on the Login page
!-- And the failed login count will increase by 1
!-- Then the failed login count for ubrn "<ubrn>" is 1


Examples:
|pageName|pageTitle|ubrn|yearofbirth|password|
|Login|e-Referral Service|123 123 123 123|1971|Password01|
|Login|e-Referral Service|1 1 1 1|1971	|Password01|
|Login|e-Referral Service|9999 9999 9999|1971|Password01|