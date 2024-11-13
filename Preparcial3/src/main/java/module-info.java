module co.ahorcadochiviado2.preparcial3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens co.ahorcadochiviado2.preparcial3 to javafx.fxml;
    exports co.ahorcadochiviado2.preparcial3;
}