package com.eb.frontend.controller;

import com.eb.signup.auth.EmailVerificationDao;
import com.eb.signup.mail.Mailer;
import com.eb.signup.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

@Controller
public class RegisterController {

    @Autowired(required = false)
    private UserDao userDao;

    @Autowired(required = false)
    private EmailVerificationDao codeDao;

    @Autowired(required = false)
    private Mailer mailer;

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String dateOfBirthStr,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword,
            Model model) {

        // Vérifier si la base de données est disponible
        if (userDao == null || codeDao == null || mailer == null) {
            model.addAttribute("error", "La base de données n'est pas disponible. Veuillez démarrer PostgreSQL et activer la connexion dans application.properties.");
            return "register";
        }

        // Validation des champs obligatoires
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.length() < 8) {
            model.addAttribute("error", "Les champs marqués d'un * sont obligatoires et le mot de passe doit contenir au moins 8 caractères");
            return "register";
        }

        // Validation de la confirmation du mot de passe
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas");
            return "register";
        }

        // Validation de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "Format d'email invalide");
            return "register";
        }

        try {
            var existing = userDao.findByEmail(email);
            if (existing != null) {
                model.addAttribute("error", "Cet email est déjà utilisé");
                return "register";
            }

            // Conversion de la date de naissance
            LocalDate dateOfBirth = null;
            if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
                try {
                    dateOfBirth = LocalDate.parse(dateOfBirthStr);
                } catch (Exception e) {
                    model.addAttribute("error", "Format de date invalide");
                    return "register";
                }
            }

            var hash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            long userId = userDao.create(firstName.trim(), lastName.trim(), email.trim(),
                    phone != null ? phone.trim() : null, dateOfBirth,
                    address != null ? address.trim() : null,
                    postalCode != null ? postalCode.trim() : null,
                    city != null ? city.trim() : null, hash);

            String code = random6();
            String codeHash = org.mindrot.jbcrypt.BCrypt.hashpw(code, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            codeDao.createCode(userId, codeHash, Instant.now().plus(Duration.ofMinutes(15)));

            // Log du code pour les tests
            System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");

            mailer.sendCode(email, code);

            // Rediriger vers la page de vérification
            return "redirect:/verify";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la création du compte : " + e.getMessage());
            return "register";
        }
    }

    private static String random6() {
        var r = new SecureRandom();
        return String.format("%06d", r.nextInt(1_000_000));
    }
}

