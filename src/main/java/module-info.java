module com.anmvg.cerenia {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires net.synedra.validatorfx;
    requires org.json;
    requires org.apache.commons.io;

    opens com.anmvg.cerenia to javafx.fxml;
    exports com.anmvg.cerenia;
    exports com.anmvg.cerenia.models;
}