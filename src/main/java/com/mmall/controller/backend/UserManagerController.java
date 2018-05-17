package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author liaocx on 2017/11/30.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    /**
     * 后台管理员用户登录接口
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //登录的是管理员用户
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }

    /**
     * admin账号拥有授权的权限
     */
    @RequestMapping(value = "authorization.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> authorization(HttpSession session, String username) {
        User admin =(User) session.getAttribute(Const.CURRENT_USER);
        if (admin.getUsername().equals(Const.Role.ROOT)) {
            return iUserService.authorization(username);
        }
        return ServerResponse.createByErrorMessage("请使用admin账号执行此操作!");
    }
}
