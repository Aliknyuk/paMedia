package com.pa.step_definitions;

import com.pa.utilityClasses.ApiUtils;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class WeatherStepDef {

    private RequestSpecification requestSpecification;
    private Response response;
    private Response responseWithDate;
    private int cityWoeidID;
    private ValidatableResponse validatableResponse;

    @Given("accept type is application-json")
    public void accept_type_is_application_json() {
      requestSpecification=given().accept(ContentType.JSON);
    }

    @When("I send a GET request to location Search web service to retrieve weather info for {string}")
    public void i_send_a_GET_request_to_location_Search_web_service_retrieve_weather_info_for(String city) {

        validatableResponse = ApiUtils.getRequestToLocationSearch(city);



    }

    @Then("I verify the statusCode is {int}")
    public void i_verify_the_statusCode_is(int expectedStatusCode) {

      validatableResponse.assertThat().statusCode(expectedStatusCode);

    }

    @Then("I verify the Content Type is {string}")
    public void i_verify_the_Content_Type_is(String expectedContentType) {
        validatableResponse.assertThat().contentType(expectedContentType);

    }

    @When("I verify the location search payload matches with the jsonSchema")
    public void i_verify_the_location_response_match_with_the_jsonSchema() {

        validatableResponse.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("locationSearchJsonSchema.json"));

    }

    @When("I verify the payload belongs to {string}")
    public void i_verify_the_payload_belongs_to(String expectedCity) {
        validatableResponse.body("title[0]",equalToIgnoringCase(expectedCity));
        validatableResponse.body("title[0]",equalToIgnoringCase(expectedCity));

    }


    @When("I send a GET request to Location web service for {string}")
    public void i_send_a_GET_request_to_location_web_service(String city) {
        validatableResponse = ApiUtils.getRequestToLocationWebService(city);
    }


    @Then("I verify the location response matches with the LocationJsonSchema")
    public void i_verify_the_location_response_match_with_the_LocationJsonSchema() {
        validatableResponse.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("weatherForCity.json"));

    }

    @Then("I verify Location response correct for {string}")
    public void i_verify_Location_response_correct_for(String expectedCityName) {

        validatableResponse.assertThat().body("title",equalToIgnoringCase(expectedCityName));
        int expectedWoeidId = ApiUtils.getWoeidNumber(expectedCityName);
        validatableResponse.assertThat().body("woeid",is(expectedWoeidId));

    }

    @When("I send a GET request to Location web service for {string} and the date of {string}")
    public void i_send_a_GET_request_to_Location_web_service_for_and_the_date_of(String city, String date) {
        validatableResponse=ApiUtils.getRequestToLocationWebService(city,date);
    }

    @When("I verify the {string} is correct")
    public void i_verify_the_is_correct(String memberName) {
        validatableResponse.assertThat()
                .body("location_type[0]",equalToIgnoringCase(memberName));

    }

    @When("I verify if the payload has those items")
    public void i_verify_if_the_payload_has_those_items(List<String> expectedMembers) {
        Map<String,Object> actualMembers = validatableResponse.extract().response().path("[0]");

//        System.out.println("expectedMembers = " + expectedMembers);
//        System.out.println("actualMembers.keySet() = " + actualMembers.keySet());

        assertTrue(actualMembers.keySet().containsAll(expectedMembers));

    }

    @Then("I verify the statusCode is NOT {int}")
    public void i_verify_the_statusCode_is_NOT(int unexpectedStatusCode) {

        int actualStatusCode = validatableResponse.extract().response().statusCode();

        assertNotEquals(unexpectedStatusCode,actualStatusCode);

    }

    @Given("I send {string}  to location Search endpoint without authentication for {string}")
    public void i_send_to_location_Search_endpoint_without_authentication_for(String requestType, String city) {
        response=ApiUtils.requestToLocationSearch(requestType,city);

    }

    @Then("I verify the status code should be {int}")
    public void i_verify_the_status_code_should_be(int expectedStatusCode) {
        assertEquals(expectedStatusCode,response.statusCode());
    }

    @Given("I send {string} request to location endpoint for {string} without authentication")
    public void i_send_request_to_location_endpoint_for_without_authentication(String requestType, String city) {

        response = ApiUtils.requestToLocationEndPoint(requestType, ApiUtils.getWoeidNumber(city));



    }




    @Then("I verify if the headers have the header which is {string}")
    public void i_verify_the_headers_have_the_header_which_is(String expectedHeader) {
        assertTrue(validatableResponse.assertThat().extract().response().headers().hasHeaderWithName(expectedHeader));

    }

    @When("I send a GET request to Location web service with woeid number for {string}")
    public void i_send_a_GET_request_to_Location_web_service_with_woeid_number_for(String woeidNum) {
        validatableResponse=ApiUtils.getRequestToLocationWebServiceWithWoeidNum(woeidNum);
    }


    @Then("I send a GET request to Location web service for {string} 's latLong")
    public void i_send_a_GET_request_to_Location_web_service_for_s_latLong(String city) {

        String latLong=ApiUtils.getLattLong(city);
        validatableResponse= ApiUtils.getRequestToLocationSearchWithLatLong(latLong);


    }

    @Then("I verify the response has {string} 's informations")
    public void i_verify_the_response_has_s_informations(String city) {
        Response expectedResponse=ApiUtils.getRequestToLocationSearch(city).extract().response();

//        System.out.println("expectedList = " + expectedList);
        Map expectedCityInfo = (Map) expectedResponse.as(List.class).get(0);

        Map actualCityInfo= (Map) validatableResponse.extract().response().as(List.class).get(0);

        assertTrue(actualCityInfo.keySet().containsAll(expectedCityInfo.keySet()));
        assertTrue(actualCityInfo.values().containsAll(expectedCityInfo.values()));

    }


    @Then("I verify the response has {string} key value")
    public void i_verify_the_response_has_key_value(String distanceKeyword) {

        response= validatableResponse.extract().response();
        Map actualCityInfo= (Map) response.as(List.class).get(0);

        assertTrue(actualCityInfo.containsKey(distanceKeyword));

    }


    @Then("I verify the response has distance value is {int} for the city")
    public void i_verify_the_response_has_distance_value_is_for(int expectedDistanceValue) {

        response= validatableResponse.extract().response();
        Map<String, Object>  actualCityInfo= (Map) response.as(List.class).get(0);
        int distance = (int) actualCityInfo.get("distance");

        Assert.assertEquals(expectedDistanceValue,distance);


    }

    @Then("I verify the latt-long search payload matches with the LatLongJSonSchema")
    public void i_verify_the_latt_long_search_payload_matches_with_the_LatLongJSonSchema() {

        validatableResponse.assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("latLongJsonSchema.json"));
    }

    @Given("accept type is HTML")
    public void accept_type_is_HTML() {
        requestSpecification=given().accept(ContentType.HTML);
    }

    @Given("accept type is xml")
    public void accept_type_is_XML() {
        requestSpecification=given().accept(ContentType.XML);
    }


}
