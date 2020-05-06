package com.nowcoder.controller;

import com.nowcoder.async.EventProducer;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/admin"}, method = RequestMethod.GET)
    public String admin() {
        return "admin";
    }

    @RequestMapping(path = {"/userList"}, method = RequestMethod.GET)
    public String userList() {
        return "userList";
    }

    @RequestMapping(path = {"/questionList"}, method = RequestMethod.GET)
    public String questionList() {
        return "questionList";
    }

    @ResponseBody
    @RequestMapping(path = {"/questionList/list"}, method = RequestMethod.GET)
    public List<Question> qslectAll() {

        return userService.selectAllQuestion();
    }

    @ResponseBody
    @RequestMapping(path = {"/userlist/list"}, method = RequestMethod.GET)
    public List<User> selectAll() {

        return userService.selectAll();
    }

    @ResponseBody
    @RequestMapping(path = {"/changeRole"}, method = RequestMethod.POST)
    public String changeRole(@RequestParam("name") String name
                            ,@RequestParam("role") String role) {

        System.out.println("changingRole");
        userService.changeRole(name,role);
        return "success";
    }
    @ResponseBody
    @RequestMapping(path = {"/changeContent"}, method = RequestMethod.POST)
    public String changeContent(@RequestParam("id") String id
            ,@RequestParam("content") String content
            ,@RequestParam("title") String title
            ,@RequestParam("content_detail") String content_detail) {

        System.out.println("changeContent");
        questionService.changeContent(Integer.parseInt(id),title,content,content_detail);
        return "success";
    }


}
