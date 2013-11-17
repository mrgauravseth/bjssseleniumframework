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

Scenario: NHSERS-112_S005_C1 | Patient Chooses not to Log Out on confirmation message
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details
!--NHSERS-112_S003_C1 | Patient Chooses to Log Out at the end of a process

!-- Given Story: NHSERS-111.Story, NHSERS-112.Story
!-- GivenStories: web_app/NHSERS-112Given.story, 
GivenStories: patient_app/NHSERS-112/NHSERS-112_S003_C1_Patient_Chooses_to_Log_Out_at_the_end_of_a_process.story

Given a patient user is logged in NHSERS-112_S004_C1
And they have selected log out
!-- And they have no unsaved data
!-- when there is no unsaved data on the <CurrentPage> page
!-- When they confirm they want to log out
When I click the no button on the <CurrentPage> page
Then the text "e-Referral Service" should be displayed on the Login page
l
Examples:
|pageName	|pageTitle		   |ubrn		  |yob		|password		|Logout	|CurrentPage 		|
|Login		|e-Referral Service|1110 0000 0001| 1971 	|Password01 	|logout	| view referral 	|

