module com.example.computer_graphics_lab3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.computer_graphics_lab3 to javafx.fxml;
    exports com.example.computer_graphics_lab3;
}