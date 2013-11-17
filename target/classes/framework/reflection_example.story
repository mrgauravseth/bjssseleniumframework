Story: Reflection example

Meta: 
@author Chi Voong
@example reflection
@skip
@framework

Scenario: reflection example with google
Given I am on the Google page
When I enter "sandwiches" in the search textbox on the Google page
And I click the submit button on the Google page
Then I get 62300000 results on the results panel on the Google page
And the current page has title of "sandwiches - Google Search" on the Google page

Scenario: reflection example with bing
Given I am on the Bing page
When I enter "sandwiches" in the search textbox on the Bing page
And I click the submit button on the Bing page
Then I get 20900000 results on the results panel on the Bing page
And the current page has title of "sandwiches - Bing" on the Bing page
