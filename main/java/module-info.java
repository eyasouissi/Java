open module tn.esprit {
    // JavaFX requirements
    requires javafx.fxml;

    // Database and security
    requires java.sql;
    requires jbcrypt;

    // Jakarta/Validation
    requires jakarta.validation;
    requires jakarta.persistence;

    // Spring
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    // Hibernate Validator
    requires org.hibernate.validator;
    requires org.glassfish.expressly;
    requires spring.web;
    requires org.kordamp.ikonli.javafx;
    requires javafx.controls;
    requires javafx.media; // Add this line
    // Export packages (still needed)
    exports tn.esprit.controllers.user.admin;
    exports tn.esprit.controllers.auth;
    exports tn.esprit.services;
}