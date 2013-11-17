Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1
@comments CV: test has been disabled since, application no longer has view all referrals screen for single referral

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S001_C1 | Patient Chooses to Log Out while updating referral	
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details

!-- Given I am on the <CurrentPage> page
!-- When I click the <ReferralButton> button on the <CurrentPage> page
!-- GivenStories: patient_app/NHSERS-112/NHSERS-112Given.story
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story

Given a patient user is logged in NHSERS-111_S001
And there is unsaved data on the <CurrentPage> page
!-- When they choose to <logout>	
When I click the <logout> button on the <CurrentPage> page
!-- When they choose to <logout>	
!-- Then message is displayed stating "Changes to your referral have not been saved. Do you want to continue without saving?"
Then the text "any updates which has not be saved will be lost" should be displayed on the <CurrentPage> page

!--unused examples
!--|Logout				|CurrentPage  		|pageName	|pageTitle|
!--|Top right		    |View All referrals	|Login		|Choose and Book|
!--|Bottom left		|View All referrals	|Login		|Choose and Book|

Examples:
|pageName	|pageTitle		   |ubrn		  |yob		|password		|logout	  	 |CurrentPage 	    |
|Login		|e-Referral Service|1110 0000 0001| 1971 	|Password01 	|top right	 |view all referrals|
|Login		|e-Referral Service|1110 0000 0001| 1971 	|Password01 	|bottom left |view all referrals|
