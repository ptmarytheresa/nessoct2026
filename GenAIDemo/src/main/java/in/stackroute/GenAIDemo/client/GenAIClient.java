package in.stackroute.GenAIDemo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenAIClient {

@Value("${OPENAI_API_KEY}")
    private String openaiapikey;


    @Value("${genai.api.url}")
    private String openaiapiurl;

    @Value("${genai.model}")
    private String openaimodel;

    private final RestTemplate restTemplate=new RestTemplate();

	@jakarta.annotation.PostConstruct
	public void init() {
	    System.out.println("API Key Loaded = "
	        + (openaiapikey != null));
	}
	
    public String callGenAI(String prompt)
    {
        HttpHeaders headers=new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiapikey);


     

        Map<String,Object> message = Map.of(
                "role","user",
                "content",prompt
        );

        Map<String,Object> body = new HashMap<>();

        body.put("model", openaimodel);
        body.put("messages", List.of(message));
        body.put("temperature", 0.7);

        HttpEntity<Map<String,Object>>  entity= new HttpEntity<>(body,headers);

        ResponseEntity<Map> response=restTemplate.postForEntity(openaiapiurl,entity,Map.class);


        List<Map<String,Object>> choices =
        		(List<Map<String,Object>>) response.getBody().get("choices");


Map<String,Object> messageResponse =
        (Map<String,Object>)
                choices.get(0).get("message");

return messageResponse
        .get("content")
        .toString();

     }

}
