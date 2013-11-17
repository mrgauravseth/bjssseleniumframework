Story: base load browser

Scenario:	NHSERS-LoadBrowser

Meta:
@author Mubsher Hussain
@sprint 1
@testphase Core 
@lastReviewed 
@acceptanceCriteria C1xxxxxxx



Given browser is open
When I navigate to <pageName> page
Then the current page has title of "<pageTitle>" on the <pageName> page

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|
