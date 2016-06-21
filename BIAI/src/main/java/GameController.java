import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import pl.RockPaperScissors.service.ImageRecognition;
import pl.RockPaperScissors.service.MainGameNetwork;
import pl.RockPaperScissors.service.Sign;

import java.io.IOException;


public class GameController {

    @FXML
    ImageView imgWebCamCapturedImage;

    @FXML
    BorderPane gameBorderPane;

    @FXML
    ImageView computerChoiceImageView;

    @FXML
    Label counter;

    @FXML
    Button startNormalCamStreamButton;

    @FXML
    Button rockPhotoToNeuralButton;

    @FXML
    Button scissorsPhotoToNeuralButton;

    @FXML
    Button paperPhotoToNeuralButton;

    @FXML
    Button learnPhotoNeuralButton;

    @FXML
    Button savePhotoNeuralButton;

    @FXML
    Button endCameraStreamButton;

    @FXML
    Button checkSignInNeuralButton;

    @FXML
    Button startGameButton;

    @FXML
    CheckBox developerModeCheckBox;

    @FXML
    Button backButton;

    @FXML
    Label resultLabel, neuralScoreLabel, countOfGamesLabel, userScoreLabel;


    ImageRecognition imageRecognition = new ImageRecognition();
    MainGameNetwork mainGameNetwork = new MainGameNetwork();
    CamController camController = new CamController(mainGameNetwork, imageRecognition);
    Integer i = 4;
    Timeline timeline;
    Sign neuralMove;
    Integer countOfGames, userScore, neuralScore;
    Main main;

    @FXML
    private void initialize() {
        countOfGames = 0;
        userScore = 0;
        neuralScore = 0;

        neuralMove = mainGameNetwork.networkMove();          // ******* ruch sieci neuronowej ***********
        mainGameNetwork.showNeuralNetworkTest();
        camController.findCamera(imgWebCamCapturedImage);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            i--;
            counter.setText(i.toString());
            if (i <= 0) {
                timeline.stop();
                counter.setText("<-->");
                getSignFromUser();
                checkComputerSelect();
                if (countOfGames < 6) startGameButton.setDisable(false);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        countOfGamesLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (countOfGames == 6) {
                startGameButton.setDisable(true);
                if (userScore > neuralScore ) resultLabel.setText("Congratulation !! You WIN");
                if (userScore < neuralScore ) resultLabel.setText("Sorry but you LOSE :(");
                if (userScore == neuralScore ) resultLabel.setText("Ohh, it's only DRAW" );
                backButton.setVisible(true);
            }
        });

    }


    public void startGame() {

        i = 4;
        if ( countOfGames > 0 ) {
            exitCheckComputerSelect();
            neuralMove = mainGameNetwork.networkMove();          // ******* ruch sieci neuronowej ***********
        }
        timeline.playFromStart();
        camController.startWebCamStream(imgWebCamCapturedImage);
        startGameButton.setDisable(true);
    }

    public void checkComputerSelect() {

        Image computerChoice;

        if (neuralMove == Sign.ROCK) computerChoice = new Image("kamien.jpg");
        else if (neuralMove == Sign.PAPER) computerChoice = new Image("papier.jpg");
        else computerChoice = new Image("nozyce.jpg");

        computerChoiceImageView.setImage(computerChoice);
    }

    public void exitCheckComputerSelect() {
        Image computerChoice = new Image("pytajnik.jpg");
        computerChoiceImageView.setImage(computerChoice);
    }

    public void turnOnDeveloperMode() {
        if (developerModeCheckBox.isSelected()) {
            startNormalCamStreamButton.setVisible(true);
            rockPhotoToNeuralButton.setVisible(true);
            scissorsPhotoToNeuralButton.setVisible(true);
            paperPhotoToNeuralButton.setVisible(true);
            learnPhotoNeuralButton.setVisible(true);
            savePhotoNeuralButton.setVisible(true);
            endCameraStreamButton.setVisible(true);
            checkSignInNeuralButton.setVisible(true);
        } else {
            startNormalCamStreamButton.setVisible(false);
            rockPhotoToNeuralButton.setVisible(false);
            scissorsPhotoToNeuralButton.setVisible(false);
            paperPhotoToNeuralButton.setVisible(false);
            learnPhotoNeuralButton.setVisible(false);
            savePhotoNeuralButton.setVisible(false);
            endCameraStreamButton.setVisible(false);
            checkSignInNeuralButton.setVisible(false);
        }
    }

    public void startNormalCamStream() {
        camController.findCamera(imgWebCamCapturedImage);
    }

    public void rockPhotoToNeural() {
        camController.takePicture(Sign.ROCK);
    }

    public void paperPhotoToNeural() {
        camController.takePicture(Sign.PAPER);
    }

    public void scissorsPhotoToNeural() {
        camController.takePicture(Sign.SCISSORS);
    }

    public void learnPhotoNeural() {
        imageRecognition.startLearningNeural();
    }

    public void savePhotoNeural() {
        imageRecognition.saveNetwork();
    }

    public void endCameraStream() {
        camController.endGame(imgWebCamCapturedImage);
    }

    public void checkSignInNeural() {
        Sign sign = camController.checkSignByNeural();
        resultLabel.setText(sign.toString());
    }

    public void getSignFromUser() {
        Sign sign = camController.getSignFromUser();
        System.out.println(sign.toString());
        mainGameNetwork.setPlayerMove(sign);

        if (neuralMove == sign) {
            resultLabel.setText("DRAW");
            countOfGames++;
            countOfGamesLabel.setText(countOfGames.toString());
        } else if (neuralMove == sign.counterSign()) {
            resultLabel.setText("LOSE");
            neuralScore++;
            neuralScoreLabel.setText(neuralScore.toString());
            countOfGames++;
            countOfGamesLabel.setText(countOfGames.toString());
        } else {
            resultLabel.setText("WIN");
            userScore++;
            userScoreLabel.setText(userScore.toString());
            countOfGames++;
            countOfGamesLabel.setText(countOfGames.toString());
        }
    }

    public void backToLogin(){
        try {
            mainGameNetwork.save();
            main.backToMainView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


