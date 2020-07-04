package com.itpaths.dam.controller.web;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class Home {

	@RequestMapping("")
	public String index() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		String uri = "http://ot-dam-dev.cheneybrothers.com:11090/otmmapi/v5/";
		//uri +="";

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("username", "tsuper");
		requestBody.add("password", "Otmm@123");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> entity = restTemplate.exchange( uri + "sessions", HttpMethod.POST, request, String.class);
		headers = entity.getHeaders();
		request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
		System.out.println(entity.getBody());


		//Response response = rootTarget.path("/v1/sessions").request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), Response.class);

		//Map cookieMap = response.getCookies();
		// Getting jsessionId from cookie
		/*String jsessionId = cookieMap.get("JSESSIONID");
		SessionRepresentation sessionRepresentation = response.readEntity(SessionRepresentation.class);
		// Getting user authentication token from response
		string otmmauthtoken = sessionRepresentation.getSession().getMessageDigest();

		// Below is code snippet to logout from OTMM
		// How to set JSESSIONID cookie into request
		rootTarget.path("/v1/sessions").request().cookie(jsessionId).delete();

		// How to set otmm authentication token into request
		rootTarget.path("/v1/sessions").request().header("otmmauthtoken",otmmauthtoken).delete()
		*/

		restTemplate.exchange(uri+"folders/rootFolder", HttpMethod.GET, request, String.class);
		String result = restTemplate.getForObject(uri + "folders/rootFolder", String.class);
		System.out.println(result);
		return "Greetings from Spring Boot!";
	}
}
