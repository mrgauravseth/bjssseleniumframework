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

!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details

!-- GivenStory - NHSERS-111.Story
!--GivenStories: patient_app/NHSERS-112/NHSERS-112Given.story
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story


!-- Given a patient user is logged in
Given a patient user is logged in NHSERS-112_S008
And have javascript enabled
!-- I click the <ReferralButton> button on the <CurrentPage> page
!-- And there is unsaved data on the <CurrentPage>
!-- And there is unsaved data on the <CurrentPage> page CV: took this out since this page is no longer accessible
!-- When they choose to <logout>
When I click the <Logout> button on the <CurrentPage> page
!-- And confirm they don't want to log out
And I click the No button on the <CurrentPage> page
!-- Then the patient user is navigated back to <CurrentPage> CV: removed since this is not accessible from referral details 
Then the text "Referral Details" should be displayed on the <CurrentPage> page
!-- Then the patient is returned to <CurrentPage>
!-- And all previously unsaved data will be retained on the <CurrentPage> page  CV: removed since this is not accessible from referral details

Examples:
|pageName	|pageTitle		   |ubrn		  |yob		|password		|Logout	|CurrentPage 		|
|Login		|e-Referral Service|1110 0000 0001| 1971 	|Password01 	|logout	| view referral	|


