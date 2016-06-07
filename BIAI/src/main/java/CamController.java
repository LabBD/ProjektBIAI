import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pl.RockPaperScissors.service.ImageRecognition;
import pl.RockPaperScissors.service.MainGameNetwork;
import pl.RockPaperScissors.service.Sign;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Mateusz on 2016-06-01.
 */
public class CamController {

    boolean stopCamera = false;
    private Webcam webcam = null;
    private BufferedImage grabbedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
    private Image imageQuestionMark = new Image("pytajnik.jpg");
    private ImageRecognition imageRecognition = new ImageRecognition();
    private MainGameNetwork mainGameNetwork;

    public CamController(MainGameNetwork mainGameNetwork) {
        this.mainGameNetwork = mainGameNetwork;
    }

    public void findCamera(ImageView imgWebCamView) {

        webcam = Webcam.getDefault();

        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
        }

        initializeWebCam(0, imgWebCamView);

    }

    protected void initializeWebCam(final int webCamIndex, final ImageView imgWebCamView) {

        Task<Void> webCamIntilizer = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if (webcam == null) {
                    webcam = Webcam.getWebcams().get(webCamIndex);
                    webcam.open();
                } else {
                    if (webcam != null) webcam.close();
                    webcam = Webcam.getWebcams().get(webCamIndex);
                    webcam.open();
                }
                startWebCamStream(imgWebCamView);
                return null;
            }

        };
        new Thread(webCamIntilizer).start();
    }


    protected void startWebCamStream(ImageView imgWebCamView) {
        stopCamera = false;

        final Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((grabbedImage = webcam.getImage()) != null) {

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    final Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
                                    imageProperty.set(mainiamge);
                                }
                            });

                            grabbedImage.flush();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamView.imageProperty().bind(imageProperty);
    }

    public void endGame(ImageView imgWebCamView) {
        stopCamera = true;
        imgWebCamView.imageProperty().unbind();
        imgWebCamView.setImage(imageQuestionMark);
        webcam.close();
        imageRecognition.saveNetwork();
        // computerChoiceImageView.setStyle("-fx-image: url('blank.jpg')");   fajnie zamienia ten drugi imageview
    }


    public void takePicture() {
        webcam.open();
//        stopCamera = true;
        BufferedImage image = webcam.getImage();
        // save image to PNG file
        File file = new File(new Date().getTime() + ".jpg");
        try {
            ImageIO.write(image, "JPG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sign sign = imageRecognition.recognizeImage(file);
        System.out.println(sign);
        mainGameNetwork.setPlayerMove(sign);
//        imageRecognition.saveImage(file, Sign.ROCK);
    }

}
