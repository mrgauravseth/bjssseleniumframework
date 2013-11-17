Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1
Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS	

Scenario: NHSERS-112_S003_C1 | Patient Chooses to Log Out at the end of a process	
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details

Given I still need to write these tests
When this functionality is supported
Then I will remove the placeholder

Examples:
|Logout			|CurrentPage  		|pageName	|pageTitle|
|Top right		|Booking Confirmation       |Login|Choose and Book|
|Bottom left	|Booking Confirmation       |Login|Choose and Book|
|Top right		|Cancel Appointment Summary |Login|Choose and Book|
|Bottom left	|Cancel Appointment Summary |Login|Choose and Book|
|Top right		|Cancel Referral            |Login|Choose and Book|
|Bottom left	|Cancel Referral            |Login|Choose and Book|
|Top right		|Deferred to Provider       |Login|Choose and Book|
|Bottom left	|Deferred to Provider       |Login|Choose and Book|
|Top right		|Booking or Rebook Failure 	|Login|Choose and Book|
|Bottom left	|Booking or Rebook Failure 	|Login|Choose and Book|
|Top right		|Cancel Appointment Failure |Login|Choose and Book|
|Bottom left	|Cancel Appointment Failure |Login|Choose and Book|



