package dev.springboot.controller;

import dev.springboot.model.StackoverflowWebsite;
import dev.springboot.service.StackoverflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.springboot.entity.model.BaseEntity;
import dev.springboot.entity.model.Poke;
import dev.springboot.persistence.facade.Facade;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/entities")
public class EntityController<T extends BaseEntity> {

    @Autowired
    private Facade facade;

      @Autowired
    private StackoverflowService stackoverflowService = new StackoverflowService();

    @RequestMapping(value = "/test")
    public List<StackoverflowWebsite> getListofProviders() throws SQLException {
        return stackoverflowService.findAll();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public
    List<T> getAllUsers() {
        return null;
    }

    @RequestMapping(value = "/getEntity", method = RequestMethod.GET, produces = "application/json")
    public
    T getUsersById(@RequestParam("id") long id, @RequestParam("CLASS") String CLASS) {
        return facade.getEntity(id, Poke.class);
    }

    @RequestMapping(value = "/getSmth", method = RequestMethod.GET, produces = "application/json")
    public
    T getSmth(@RequestParam("id") long id) {
        return facade.getPok(id);
    }

    @RequestMapping(value = "/addEntity", method = RequestMethod.POST, produces = "application/json")
    public void addUser(@RequestBody T entity) {
        facade.createEntity(entity);
    }

    @RequestMapping(value = "/deleteEntity", method = RequestMethod.DELETE)
    public
    T deleteUser(@RequestParam("id") long id) {
        return null;
    }
}
