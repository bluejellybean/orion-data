package org.kpmp.users;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

	private UserService userService;
	private JWTHandler jwtHandler;
	private LoggingService logger;

	@Autowired
	public UserController(UserService userService, JWTHandler jwtHandler, LoggingService logger) {
		this.userService = userService;
		this.jwtHandler = jwtHandler;
		this.logger = logger;
	}

	@RequestMapping(value = "/v1/users", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsers(
			@RequestParam(value = "hasPackage", defaultValue = "false") String hasPackage, HttpServletRequest request) {
		List<User> users;
		if (hasPackage.equals("true")) {
			logger.logInfoMessage(this.getClass(), jwtHandler.getUserIdFromHeader(request), null,
					request.getRequestURI(), "Getting users with packages");
			users = userService.findAllWithPackages();
		} else {
			logger.logInfoMessage(this.getClass(), jwtHandler.getUserIdFromHeader(request), null,
					request.getRequestURI(), "Getting all users");
			users = userService.findAll();
		}
		return users;
	}

}
