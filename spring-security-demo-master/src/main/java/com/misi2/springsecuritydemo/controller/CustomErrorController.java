package com.misi2.springsecuritydemo.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            model.addAttribute("statusCode", statusCode);

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorTitle", "Accès Refusé");
                model.addAttribute("errorMessage",
                        "Vous n'avez pas les permissions nécessaires pour accéder à cette page.");
                model.addAttribute("errorIcon", "bi-shield-lock");
                return "error/403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Page Non Trouvée");
                model.addAttribute("errorMessage", "La page que vous recherchez n'existe pas ou a été déplacée.");
                model.addAttribute("errorIcon", "bi-search");
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Erreur Serveur");
                model.addAttribute("errorMessage", "Une erreur interne s'est produite. Veuillez réessayer plus tard.");
                model.addAttribute("errorIcon", "bi-exclamation-triangle");
                return "error/500";
            }
        }

        // Default error page
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorTitle", "Erreur");
        model.addAttribute("errorMessage", "Une erreur inattendue s'est produite.");
        model.addAttribute("errorIcon", "bi-exclamation-circle");
        return "error/error";
    }
}
