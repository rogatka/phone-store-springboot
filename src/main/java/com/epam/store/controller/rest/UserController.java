package com.epam.store.controller.rest;

import com.epam.store.entity.User;
import com.epam.store.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "users")
public class UserController {
    private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll(@RequestParam(name = "sortBy", required = false) String sortVariable,
                                              @RequestParam(name = "name", required = false) String searchVariable) {
        if (sortVariable == null && searchVariable == null) {
            return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
        }
        List<User> users;
        if (sortVariable != null) {
            switch (sortVariable.trim()) {
                case "firstName":
                    users = userService.findAllSortByFirstName();
                    break;
                case "lastName":
                    users = userService.findAllSortByLastName();
                    break;
                default:
                    users = userService.findAll();
            }
            if (searchVariable != null) {
                List<User> userList = userService.findAllByName(searchVariable);
                users.retainAll(userList);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
            users = userService.findAllByName(searchVariable);
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> findById(@PathVariable(name = "id") Long id) throws NotFoundException {
        Optional<User> userResult = userService.findById(id);
        if (userResult.isPresent()) {
            return new ResponseEntity<>(userResult.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("user with id = " + id + " not exist");
        }
    }

    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.save(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@RequestBody User user, @PathVariable Long id) throws NotFoundException {
        if (userService.findById(id).isPresent()) {
            userService.save(user);
        } else {
            throw new NotFoundException("user with id = " + user.getId() + " not exist");
        }
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable(name = "id") Long id) throws NotFoundException {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
        } else {
            throw new NotFoundException("user with id = " + id + " not exist");
        }
    }
}
