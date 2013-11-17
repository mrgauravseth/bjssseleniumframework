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
 
!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details

!--GivenStories: patient_app/NHSERS-112/NHSERS-112Given.story
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story
	
Given a patient user is logged in NHSERS-112_S003
!-- When I click the <ReferralButton> button on the <CurrentPage> page
And they are at the end of the process on <CurrentPage>
!-- And there is no unsaved data
!-- And there is no unsaved data on the <CurrentPage> page  CV: removed step for now, since this page (view all referrals) is not reachable
!-- When they choose to <logout >
When I click the <Logout> button on the <CurrentPage> page
Then the logout confirmation popup window should appear on the <CurrentPage> page
!-- Then message is displayed stating "Are you sure you want to exit Choose and Book?"
!--Then the text "Are you sure you want to exit Choose and Book?" should be displayed on the <CurrentPage> page  CV: removed step since error message text has changed
Then the text "Are you sure you want to log out?" should be displayed on the <CurrentPage> page

Examples:
|pageName	|pageTitle		   |ubrn		  |yob		|password		|Logout	| CurrentPage 		|
|Login		|e-Referral Service|1110 0000 0001| 1971 	|Password01 	|logout	| view referral 	|

