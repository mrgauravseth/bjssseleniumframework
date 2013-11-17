Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C1xxxxxxx
@scenario NHSERS-111_S001_C00_Happy_Path_Login
		
Narrative:	
In order to manage my Appointment Request(s)
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S001_C00_Happy_Path_Login

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "e-Referral Service" on the Login page
!-- Given I am on the Login page
!-- Given a patilent user had a valid <UBRN>
When I enter "<ubrn>" in the ubrn textboxes on the Login page
!-- And a valid <year of birth>
And I enter "<yob>" in the yobField textbox on the Login page
!-- And a valid <password> 
And I enter "<password>" in the passwordField textbox on the Login page
!-- Given is not locked out		
!-- When they submit their login details on the login page
And I click the login button on the Login page
!-- Then the patient user is able to successfully login to ERS displaying appropriate appointment request
!--Then the patient user is able to successfully login to ERS
Then the current page has title of "<pageTitle>" on the <pageName> page


Examples:
|pageName|pageTitle|ubrn | yob	|password	|	
|Login|e-Referral Service|1110 0000 0001| 1971 |Password01|

