module org.dsc.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires java.sql;
    requires com.fasterxml.jackson.core;

    opens org.dsc.main to javafx.fxml;
    exports org.dsc.main;

}