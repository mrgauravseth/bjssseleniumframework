package uk.nhs.ers.techtest.steps;

import java.io.IOException;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Aliases;
import org.junit.Assert;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.nhs.ers.techtest.util.HttpService;
import uk.nhs.ers.techtest.util.SpringPropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * $p
 * A simple POJO, which will contain the Java methods that are mapped to the textual steps
 * The methods need to annotated with one of the JBehave annotations and the annotated value
 * should contain a regex pattern that matches the textual step$/
 * 
 * Rest Service api calls to gateways:
 *  SSB
 *  PDS
 *  SDS
 * 
 */
public final class GatewayHttpSteps {	

   private String urlStr;
   private String authenticationIP;
   private String authenticationUrl;
   private HttpService httpService = new HttpService();
   private String[] response = new String[2];
   private String httpResponseCode;
   private String httpResponse;
   private static final Logger logger = LoggerFactory.getLogger(GatewayHttpSteps.class);	 
   private String restService;
   private String method;
 
	public GatewayHttpSteps() {
  		if (logger.isDebugEnabled()) {
	       logger.debug("*** GatewayHttpSteps ***");
		}  	  
  		setUp();
	}
	/**
	 * Retrieve the gateway url property 
	 */
	public void setUp() {
  		authenticationIP = SpringPropertiesUtil.getProperty("authenticationIP"); 

  		if (authenticationIP.equals(null)) {
  			System.err.println("*** error: authentication properties not found ***");
  			System.exit(1);  			
  		} 
  		authenticationUrl = authenticationIP + ":8080";
  		if (logger.isDebugEnabled())   		{
 		    logger.debug("*** authenticationUrl: " + authenticationUrl);
 		}  
	}
  /*
  //Scenario: NHSERS-307_S001 
    @Given("a professional user has a SAML file on the Mock SSB of $SAMLFileName")
    @Aliases(values={"a patient user has been imported into PDS via a JSON file $NhsId.json",
                     "NHS Person <Uid> has associated Business Function Codes $FunctionCodes"})
    public void fileExists(String fileName){
    	if (logger.isDebugEnabled()) 		{
     	    logger.debug("*** Gateway fileExists() ***");
		} 
    }

    @Given("a professional user does not have a SAML file on the Mock SSB of $SAMLFileName")
    @Aliases(values={"a patient user does not exist in PDS with a NHS Id of $NhsId",
                     "NHS Person $Uid does not exist in the SDS",
                     "And NHS Person <Uid> has no Business Function Codes in the SDS"})   
    public void fileDoesNotExist(String fileName){
    	if (logger.isDebugEnabled()) 		{
     	    logger.debug("*** fileDoesNotExist() *** " + fileName );
		} 
    }    

    @Given("NHS Person $Uid has no Business Function Codes in the SDS") 
    public void personHasNoBusinessFunctionCodes(String uid){
    	if (logger.isDebugEnabled()) 		{
     	    logger.debug("*** personHasNoBusinessFunctionCodes() *** " + uid);
		} 
    }
    */
    /**
     * Check tomcat is running   
     */
    @Given("the Mock SSB Gateway is running")
    @Aliases(values={"the PDS Gateway is running",
                     "the SDS API service is running"})
    public void serviceRunning() {
     	if (logger.isDebugEnabled()) {
     	    logger.debug("*** serviceRunning() ***");
		    logger.debug("*** authenticationUrl *** " + authenticationUrl);
		}  	
     	
    	try {	    		
			httpService.serviceRunning(authenticationUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("*** Steps - Service is unavailable");
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
    }
    /**
     * 
     * 
     * Check tomcat service is not running
     */
    @Given("the Mock SSB Gateway is not running")
    public void SSBNotRunning() {
     	if (logger.isDebugEnabled()) {
 		    logger.debug("*** SSBNotRunning() ***");
 	}     	  

 //    	authenticationUrl =  "http://localhost:1234";
    	authenticationUrl =  "http://12.34.56.78:1234";
    	try {	    		
			httpService.serviceRunning(authenticationUrl + "/ers-ssb-gateway/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("*** Steps - ers-ssb-gateway service is unavailable:");
			System.err.println(e.getMessage());
		}
    } 
    /**
     * 
     * Call the HttpService class, retrieve the response code and response   
     * @param String restService - Restservice call from the examples table
     * @param String method - Http call to be performed, from the examples of table  
     * @param, String port - Port 8080 or 8443
     */
    @When("the rest service $RestService for this user is called with the $Method method on port $port")
    public void restServiceCall(String restService, String method, String port) {
     	if (logger.isDebugEnabled()) {
 		   logger.debug("*** restServiceCall(): "  + "/" + port + "/" + method + "/" + restService );
 		}  
     	
     	switch (port) {
     		case "8080":
     			authenticationUrl = authenticationIP + ":8080";
     			break;
     		case "8443":
	 			authenticationUrl = authenticationIP + ":8443";
	 			break;
        	default:
            	System.err.println("Error - Invalid port specified: " + port);
            	System.exit(1);
     	}

    	urlStr = authenticationUrl + restService;
    	urlStr = urlStr.replaceAll(" ","%20");   	
 
    	if (logger.isDebugEnabled()) {
		    logger.debug("*** restServiceCall: "  + urlStr);
		}   
     	try {
    	  switch (method) {
    		case "GET":
    			 response = httpService.httpGet(urlStr);     
    			 break;
    		case "POST":
    			 response = httpService.httpPost(urlStr);     			   
        		 break;
    	    case "PUT":
     			 response = httpService.httpPut(urlStr);
     			 break;
     		case "DELETE":
        		 response = httpService.httpDelete(urlStr);
        		 break;
        	case "INVALID":
        		 response = httpService.httpInvalid(urlStr);
            	 break;
   /*       case "TRACE":
                 response = httpService.httpTrace(urlStr);
                 break;  */ 
        	default:
            	System.err.println("Http method not defined for test: " + authenticationUrl + " " 
            			                                                + method + " " +restService);
            	
            	System.exit(1);
    	  }
			httpResponseCode = response[0];
			httpResponse = response[1];    	  
     	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		System.err.println("*** Steps -  Authentication service call failure");
    		System.err.println("*** URL:" + urlStr);
			System.err.println(e.getMessage());
    		e.printStackTrace();
    		System.exit(1);
    	}
    } 
    /**
     * Check HTTP response with the response in the examples tables 
     * @param String HTTP result from the examples table
     */
    @Then("the HTTP result is $HTTPResult")
    public void checkResponseCode(String httpResult) {
    	 if (logger.isDebugEnabled()) {
			 logger.debug("*** checkResponseCode(): " + httpResult);
		 }
    	 Assert.assertEquals(" *** Unexpected http response *** " + httpResult , httpResult, httpResponseCode);
    }
    /**
     * Check JSON response with the response in the examples tables 
     * @param String Json result from the examples table
     */ 
    @Then("the JSON result is $JSONResult")
    public void checkResponse(String jsonResult) {
    	 if (logger.isDebugEnabled()) {
			 logger.debug("*** checkResponse() ***");
			 logger.debug("*** jsonResult:  " +  jsonResult);
			 logger.debug("*** httpResponse:" +  httpResponse);
		 } 
    	 Assert.assertEquals(" *** Unexpected JSON response ***", jsonResult, httpResponse);
	}
    /**
     * Check JSON response with the response in the examples tables, 
     * exclude the check of the time within the timestamp 
     * @param String Json result from the examples table
     */ 
    @Then("the JSON result excluding current time is $JSONResult")
    public void checkSDSResponse(String jsonResult) {
    	 String jsonResultSubstr;
    	 String httpResponseSubstr;
    	 String httpDate;
    	 String currentDate;
    	 int pos;
    	 int posDateStart;
    	 int posDateEnd;
    	 
    	 if (logger.isDebugEnabled()) {
			 logger.debug("*** checkSDSResponse() ***");
			 logger.debug("*** jsonResult:  " +  jsonResult);
			 logger.debug("*** httpResponse:" +  httpResponse);
		 } 
    	 pos = httpResponse.indexOf("\"date\"");
    	 httpResponseSubstr = httpResponse.substring(0,pos);
    	 jsonResultSubstr = jsonResult.substring(0,pos);
    	 Assert.assertEquals(" *** Unexpected JSON response ***", jsonResultSubstr, httpResponseSubstr);
    
    	 currentDate = getCurrentDate();
    	 posDateStart = pos + 8;
    	 posDateEnd = pos + 18;
    	 httpDate = httpResponse.substring(posDateStart, posDateEnd);
    	 Assert.assertEquals(" *** Response date is not current date ***", httpDate, currentDate);
  
    	 pos = httpResponse.indexOf("\"ordered");
    	 httpResponseSubstr = httpResponse.substring(pos);
    	 jsonResultSubstr = jsonResult.substring(pos);  
    	 Assert.assertEquals(" *** Unexpected JSON response ***", jsonResultSubstr, httpResponseSubstr);
	 }
    /**
     * 
     * Retrieve the current date
     * @return String Current date
     */
    public String getCurrentDate() {
    	 if (logger.isDebugEnabled()) {
			 logger.debug("*** getCurrentDate() ***");
		 } 
		 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 Date date = new Date();
		 return dateFormat.format(date);    	
    }
} 