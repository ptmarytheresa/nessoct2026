package in.stackroute.GenAIDemo.service;

import org.springframework.stereotype.Service;

import in.stackroute.GenAIDemo.client.GenAIClient;

@Service

public class GenAIService {

    private final GenAIClient genAIClient;

   public  GenAIService(GenAIClient genAIClient)
   {
       this.genAIClient=genAIClient;
   }


   public String generateResponse(String prompt)
   {
       return genAIClient.callGenAI(prompt);
   }
}
