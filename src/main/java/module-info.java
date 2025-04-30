module mentor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens esprit to javafx.fxml;
    opens esprit.controllers.group to javafx.fxml;
    opens esprit.entities.group to javafx.base;
    opens esprit.entities.project to javafx.base;
    opens esprit.controllers.project to javafx.fxml; // Ouvre le package pour javafx.fxml

    exports esprit;

    requires jakarta.persistence;
    requires java.sql; // utile si tu utilises des types comme LocalDate avec JPA
    requires jakarta.validation;
    requires org.fxmisc.richtext;
    requires javafx.web;
    requires java.desktop; 
    requires jdk.jsobject; 

        // Export packages (still needed)
    exports esprit.controllers.group;
    exports esprit.services.group;
    exports esprit.controllers.project; // Exporte le package contenant le contrôleur

}
