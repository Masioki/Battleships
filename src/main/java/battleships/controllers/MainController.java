package battleships.controllers;

import battleships.domain.user.AbstractUser;
import battleships.domain.user.UserRole;
import battleships.dto.LoginData;
import battleships.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class MainController {

    @Autowired
    private UserService service;


    @GetMapping("/")
    public String getMainPage(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "index";
    }


    @GetMapping(value = "/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "register";
    }


    @PostMapping(value = "/register")
    public String register(@Valid @ModelAttribute("user") LoginData dto, BindingResult result, Model model, RedirectAttributes atr) {
        if (result.hasErrors()) {
            atr.addAttribute("baddata");
            return "register";
        }
        try {
            service.registerUser(dto, UserRole.USER);
        } catch (Exception e) {
            atr.addAttribute("userexists");
            return "register";
        }
        // model.addAttribute("user", dto);
        return "redirect:/";
    }
}
