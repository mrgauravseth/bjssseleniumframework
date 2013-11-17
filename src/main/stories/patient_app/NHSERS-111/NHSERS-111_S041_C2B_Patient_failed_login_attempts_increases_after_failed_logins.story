Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2B

		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S041_C2B | Patient failed login attempts increases after failed logins
!--Pre-requisites
!--NHSERS-111_S007_C2B_Patient_should_be_unable_to_login_with_invalid_Year_of_birth

GivenStories: patient_app/NHSERS-111/NHSERS-111_S009_C2B_Patient_should_be_unable_to_login_with_invalid_password.story
!-- Given a Patient User has a valid <UBRN>
Given the failed login count for ubrn "<ubrnno>" is 1
When I enter "<ubrn>" in the ubrn textboxes on the Login page
!-- And an invalid <Year of Birth>	
And I enter "<yearofbirth>" in the yobField textbox on the Login page
!-- And a valid <Password>	
And I enter "<password>" in the passwordField textbox on the Login page
!-- And 1 failed login attempt
!-- When they submit their login details on the login pagel
And I click the login button on the Login page
!-- Then the  Patient User is unable to login	
!-- And the Login Page will be shown
Then the Login Page will be shown	
!-- And "Access to choose and book has been denied because the appointment reference number, year of birth or password are incorrect. Please check these 		
!-- and try again. If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will  
!-- be able to help you" error message displayed	
!-- And the text "Access to Choose and Book has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again" should be displayed on the Login page
!-- And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page
And the text "Access to e-Referral Service has been denied because the appointment Reference number, Year of birth or Password are incorrect. Please check these and try again." should be displayed on the Login page
And the text "If you no longer have your password you should return to your GP practice to have it reissued. The receptionist or other practice staff will be able to help you." should be displayed on the Login page
!-- And the Activity log is updated with failed login attempt
!-- And the failed login attempts will increase to 2
And the failed login count for ubrn "<ubrnno>" is 2

!-- unused table examples
!-- |Login|e-Referral Service|1110 0000 0001|1110 0000 0001|1971|Password01|
!-- |Login|e-Referral Service|1110 0000 0011|1110 0000 0001|2014|Password01|
!-- |Login|e-Referral Service|1110 0000 0012|1110 0000 0001|19YY|Password01|
!-- |Login|e-Referral Service|1110 0000 0013|1110 0000 0001|YY85|Password01|

Examples:
|pageName|pageTitle|ubrn|ubrnno|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0019|111000000019|1971|WrongPass|
