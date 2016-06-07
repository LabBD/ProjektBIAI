import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GameController {

    @FXML
    ImageView imgWebCamCapturedImage;

    @FXML
    Button startCamButton;

    @FXML
    Button endGameButton;

    @FXML
    BorderPane gameBorderPane;

    @FXML
    Button takePhotoButton;

    @FXML
    ImageView computerChoiceImageView;

    @FXML
    Label counter;

    CamController camController = new CamController();
    Integer i = 0;

    @FXML
    private void initialize()
    {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            i++;
            counter.setText(i.toString());
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void findCamera() {
        camController.findCamera(imgWebCamCapturedImage);
        startCamButton.setDisable(true);
        endGameButton.setDisable(false);
        takePhotoButton.setDisable(false);
    }

    public void endGame(){
        camController.endGame(imgWebCamCapturedImage);
        startCamButton.setDisable(false);
        endGameButton.setDisable(true);
        takePhotoButton.setDisable(true);
    }


    public void takePicture() {
        camController.takePicture();
        startCamButton.setDisable(false);
    }

    public void checkComputerSelect(){
        Image computerChoice;
        Random generator = new Random();
        int computer;

        computer = generator.nextInt(3);

        if( computer == 0 ) computerChoice = new Image("kamien.jpg");
        else if ( computer == 1 ) computerChoice = new Image("papier.jpg");
            else computerChoice = new Image("nozyce.jpg");

        computerChoiceImageView.setImage(computerChoice);
    }

    public void exitCheckComputerSelect(){
        Image computerChoice = new Image("pytajnik.jpg");
        computerChoiceImageView.setImage(computerChoice);
    }



    class MyTimerTask extends TimerTask
    {
        public void run()
        {
            i++;
            counter.setText(i.toString());
        }
    }



}
