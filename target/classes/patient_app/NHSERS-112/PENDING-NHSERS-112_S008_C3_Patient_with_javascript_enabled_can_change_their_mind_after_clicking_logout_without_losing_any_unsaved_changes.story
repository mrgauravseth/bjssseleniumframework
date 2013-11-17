Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C3

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S008_C3 | Patient with javascript enabled can change their mind after clicking logout without losing any unsaved changes
Meta:
@pending

Given I still need to write these tests
When this functionality is supported
Then I will remove the placeholder

Examples:
|Logout			|CurrentPage 			|pageName	|pageTitle|
|Top right		|Booking referral  		|Login		|Choose and Book|
|Bottom left	|Booking referral 		|Login		|Choose and Book|
|Top right		|Rebooking referral  	|Login		|Choose and Book|
|Bottom left	|Rebooking referral 	|Login		|Choose and Book| 
|Top right		|Cancelling referral  	|Login		|Choose and Book|
|Bottom left	|Cancelling referral 	|Login		|Choose and Book|
|Top right		|Deferring referral  	|Login		|Choose and Book|
|Bottom left	|Deferring referral 	|Login		|Choose and Book|


