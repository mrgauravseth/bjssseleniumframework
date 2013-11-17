Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B
@bug JIRA 1383
@comment CV: currently does not work as the failed_login_count is not incremented for incorrect year of birth

Narrative:	
In order to manage my Appointment Request(s)
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S07_C2B | Patient should be unable to login with invalid Year of birth
 
GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User has a valid <UBRN>	
When I enter "<ubrn>" in the ubrn textboxes on the Login page
!-- And an invalid <Year of Birth>	
And I enter "<yearofbirth>" in the yobField textbox on the Login page
!-- And a valid <Password>
And I enter "<password>" in the passwordField textbox on the Login page	
!-- When they submit their login details on the login page
And I click the login button on the Login page		
!-- Then the  Patient User is unable to login	
!-- And the Login Page will be shown
Then the Login Page will be shown
!-- And "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these 		
!-- and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will  
!-- be able to help you" error message displayed
And the text "Access to Choose and Book has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again" should be displayed on the Login page
And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page
!-- And failed login attempts increases
And the failed login count for ubrn "<ubrn>" is 1


Examples:
|pageName|pageTitle|ubrn|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0001|999|Password01|
|Login|e-Referral Service|1130 0000 0011|2014|Password01|
|Login|e-Referral Service|1130 0000 0012|19YY|Password01|
|Login|e-Referral Service|1130 0000 0013|YY85|Password01|
