module com.example.assignment2gc200579623 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;


    opens com.example.assignment2gc200579623 to javafx.fxml;
    exports com.example.assignment2gc200579623;
}