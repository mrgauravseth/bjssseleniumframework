Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1c
@manualonly

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S007_C1c | Patient with javascript disabled can change their mind after clicking logout and will lose any unsaved changes


!--Pre-requisites
!--Given Story: NHSE-RS-111_S001_C1 | Patient Logs in with valid Details	

Given Story: NHSE-RS-111.Story
	
Given a patient user is logged in
And have javascript disabled	
And there is unsaved data on the <CurrentPage>
When they choose to <logout>
And confirm they don't want to log out
Then the patient user is navigated back to <CurrentPage>	
And all previously unsaved data will be lost
		
Examples:
|Logout			|CurrentPage 		| pageName	|pageTitle|
|Top right		|Booking referral  	|Login		|Choose and Book|
|Bottom left	|Booking referral 	|Login		|Choose and Book|
|Top right		|Rebooking referral |Login		|Choose and Book|
|Bottom left	|Rebooking referral |Login		|Choose and Book|
|Top right		|Cancelling referral|Login		|Choose and Book|
|Bottom left	|Cancelling referral|Login		|Choose and Book|
|Top right		|Deferring referral |Login		|Choose and Book|
|Bottom left	|Deferring referral |Login		|Choose and Book| 

