Story: NHSERS-117 Patient Views Referral  

Meta:
@author Gaurav Seth
@sprint 1
@testphase Core Regression 
@lastReviewed
@acceptanceCriteria c1

Narrative:
In order to check my referral
As a Patient User 
I want to view my referral in e-RS 


Scenario: NHSERS-117_S004_C3 | Actions based on a Directly Bookable TAS Appointment Booking that is Deferred to Provider selected from a list of Referrals

!-- Pre-requisites
!-- NHSERS-111_S001 PatientLoginSuccess
!-- NHSERS-114_S016 Patient should be able to progress an Unbooked (Deferred to Provider), Directly Bookable, Telephone Assessment referral
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story
!-- Given that the Patient is logged in to eRS
Given I am on the view referral page
When I click the view all Referrals button on the view referral page
!-- And has selected an Appointment from the referral list with <UBRN> with <Status> <Service Type> <Appointment Type> <Appointment Date/Time>
And I click the <referral> radio option on the View Referral page
!-- And I click the "<UBRN>" radio option value on radio option<> on the view referral page
!-- When the Patient selects the Continue button
And I click the continue Referral button on the View Referral page
Then I am on the view referral page
And the text "The details of your appointment are shown below." should NOT be displayed on the view referral page
!-- Then details of the Appointment Request cannot be seen
!-- And an appropriate placeholder page is seen - CV: there is no placeholder, just a blank screen

Examples:
|pageName	|pageTitle		|ubrn 			| yob	|password	|referral|	
|Login		|e-Referral Service|1170 0000 0041| 1971 	|Password01	|referral 117000000041|
