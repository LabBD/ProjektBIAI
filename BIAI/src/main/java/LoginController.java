import com.sun.deploy.util.FXLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    Button btnStart;

    @FXML
    Label errorLabelInLogin;

    @FXML
    TextField nicknameTextField;

    private Main main;

    @FXML
    public void checkNickname() throws IOException {
        if (nicknameTextField.getText().equals("")) {
            errorLabelInLogin.setText("Please insert your nickname.");
        } else {
            main.showGameView();
        }
    }
}
