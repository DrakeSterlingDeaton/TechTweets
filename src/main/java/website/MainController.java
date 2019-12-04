package website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
public class MainController {

    public static void main(String[] args) {
        SpringApplication.run(MainController.class, args);
    }

    @GetMapping("/")
    public ModelAndView greetingResponse(){
        return new ModelAndView("index.html");
    }

}
