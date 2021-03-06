package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = RequestMethod.GET)
    public String getConversationList(Model model) {
        if (hostHolder.getUser() == null) {
            return "redirect:/relogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
        List<ViewObject> conversations = new ArrayList<>();
        for (Message message : conversationList) {
            ViewObject vo = new ViewObject();
            vo.set("message", message);
            int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
            vo.set("user", userService.getUser(targetId));
            vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations", conversations);
        return "letter";
    }


    @RequestMapping(path = {"/msg/detail"}, method = RequestMethod.GET)
    public String getConversationDetail(Model model,
                                        @RequestParam("to_id") int to_id) {


        model.addAttribute("to_user",userService.getUser(to_id));
            try {

                String iD = hostHolder.getUser().getId()+"_"+to_id;
                String iD2 = to_id+"_"+hostHolder.getUser().getId();
                List<Message> messageList = messageService.getConversationDetail(iD,0, 10);
                List<Message> messageList2 = messageService.getConversationDetail(iD2,0, 10);

                List<ViewObject> messages = new ArrayList<>();
                for (Message message : messageList) {
                    messageService.updateHasRead(hostHolder.getUser().getId()); //将已查看的message改为已读
                    ViewObject vo = new ViewObject();
                    vo.set("message", message);
                    vo.set("user", userService.getUser(message.getFromId()));
                    messages.add(vo);
                }
                for (Message message : messageList2) {
                    messageService.updateHasRead(hostHolder.getUser().getId()); //将已查看的message改为已读
                    ViewObject vo = new ViewObject();
                    vo.set("message", message);
                    vo.set("user", userService.getUser(message.getFromId()));
                    messages.add(vo);
                }


                model.addAttribute("messages", messages);


            } catch (Exception e) {
                logger.error("获取消息详情失败!" + e.getMessage());
            }


        return "letterDetail";
    }

    @ResponseBody
    @RequestMapping(path = {"/msg/addMessage"}, method = RequestMethod.POST)
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        System.out.println("1");

        try {
            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "未登录!");
            }
            User user = userService.selectByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在!");
            }
            Message message = new Message();
            message.setFromId(hostHolder.getUser().getId());
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setToId(user.getId());
            messageService.addMessage(message);

            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("发送消息失败!" +e.getMessage());
            return WendaUtil.getJSONString(1, "发送消息失败!");

        }
    }
}
