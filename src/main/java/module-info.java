open module tn.esprit {
    // JavaFX requirements
    requires javafx.controls;
    requires javafx.fxml;

    // Database and security
    requires java.sql;
    requires jbcrypt;
    requires javafx.web; // For HostServices
    // Jakarta/Validation
    requires jakarta.validation;
    requires jakarta.persistence;
    requires java.desktop;
    // Spring
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    // Hibernate Validator
    requires org.hibernate.validator;
    requires org.glassfish.expressly;
    requires spring.web;
    requires spring.data.commons;
    requires org.hibernate.orm.core; // Add this line
    // Export packages (still needed)
    exports tn.esprit.controllers.user.admin;
    exports tn.esprit.controllers.auth;
    exports tn.esprit.services;
}