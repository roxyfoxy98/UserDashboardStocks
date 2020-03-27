package net.cs50.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class Cs50FinanceApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Cs50FinanceApplication.class, args);
        
        // for debugging. On start up, Log to the console a list of all the beans that Spring has created behind the scenes
        /*
        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
        */

    }
}
