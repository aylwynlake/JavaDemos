package ly.test.springmvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/home")
public class HomeController {
    private Log log = LogFactory.getLog(HomeController.class);
    
    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        log.info("welcome");
        return "home";
    }
}
