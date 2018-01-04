package dev.springboot.web;

import dev.springboot.model.StackoverflowWebsite;
import dev.springboot.service.StackoverflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

//@RestController
//@RequestMapping("/api/stackoverflow")
public class StackOverflowController {
  //  @Autowired
    private StackoverflowService stackoverflowService = new StackoverflowService();

    //@RequestMapping
    public List<StackoverflowWebsite> getListofProviders() throws SQLException{
        return stackoverflowService.findAll();
    }
}
