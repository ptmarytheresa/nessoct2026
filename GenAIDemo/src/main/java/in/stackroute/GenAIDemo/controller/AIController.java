package in.stackroute.GenAIDemo.controller;

import in.stackroute.GenAIDemo.service.GenAIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genai")
public class AIController {

	public record ChatRequest(String prompt){}
    private final GenAIService service;


            public AIController(GenAIService service)
            {
                this.service=service;
            }

//            1. Chatbot Endpoint
    @PostMapping("/chat")
public String chatWithAI(@RequestBody String  prompt)
    {
//        System.out.println(prompt);
        return service.generateResponse(prompt);
    }
    


}
