Story: NHSERS-113 Patient Views Unbooked Referral

Narrative:
In order to progress an Appointment Request that does not yet have an Appointment Booking
As a Patient User
I want to view my Appointment Request
					 
Scenario: NHSERS-113_S001_C1_Patient_Views_Unbooked_Referral_with_Routine_Priority_that_all_available_Services

Meta:
@author Zak Timms
@sprint 1
@testphase Core 
@developedFromJIRA 113 22/10/13 5:01 PM
@lastReviewed
@acceptanceCriteria C1xxxxxxx
!-- Pre-requisites
!-- NHSERS-111_S001 PatientLoginSuccess


GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story

!--Given that the Patient has a list of Referrals Booked and Unbooked
!--And is logged in to eRS with <UBRN> with <Referral Status> <Priority> <Shortlisted Services> <Service Name> <Referring Clinician> <Additional Requirements> <Referring Organisation>
Given I am on the <CurrentPage> page
!--When the Referral Details page is presented
!--Then details relating to the <UBRN> are displayed
!--And the Patient can view context specific configurable text "This screen lists the clinic(s) that you can choose from to book your appointment. Click on 'Book Appointment' to select a clinic and appointment time. If the appointment is no longer needed, click 'Cancel Referral'. If this is not the referral you want to change, click 'View All Referrals'.
When I view the contents of the <CurrentPage> page
Then the text "This screen lists the clinic(s) that you can choose from to book your appointment. Click on 'Book Appointment' to select a clinic and appointment time. If the appointment is no longer needed, click 'Cancel Referral'. If this is not the referral you want to change, click 'View All Referrals" should be displayed on the <CurrentPage> page
!--And the text "Clinics : " followed by a list of all <Shortlisted Services> is displayed
And the text "Clinics:" should be displayed on the "<CurrentPage>" page
And the text "DB_AVAIL_F2F_SERVICE1:" should be displayed on the <CurrentPage> page
And the text "DB_AVAIL_F2F_SERVICE2:" should be displayed on the <CurrentPage> page
And the text "INDB_AVAIL_F2F_SERVICE1:" should be displayed on the <CurrentPage> page
!--And the text "Referred by: " is displayed followed by the name of <Referring Clinician> who initiated the Appointment Request
And the text "Referred by: " should be displayed on the <CurrentPage> page
And the text "CLIN1" should be displayed on the <CurrentPage> page
!--And the button "Book Appointment" is displayed
!--And the button "Cancel Referral" is displayed
!--And the button "View All Referrals" is displayed
And the Button "book Appointment" should be displayed on the <CurrentPage> page
And the Button "cancel Appointment" should be displayed on the <CurrentPage> page
And the Button "view All Referrals" should be displayed on the <CurrentPage> page



Examples:
|UBRN 			|yob  | password   |Referral Status        |Priority|Shortlisted Services |Service Name|ReferringClinician |Additional Requirements|Referring Organisation  |CurrentPage      |pageName|pageTitle|
|1130 0000 0011 |1971 | Password01 |Unbooked (Never Booked)|Routine |>1 and All Available |None        |CLINI               |None                   |Known                   |View Referral    |Login|e-Referral Service| 


