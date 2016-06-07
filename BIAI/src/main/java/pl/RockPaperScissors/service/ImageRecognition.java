package pl.RockPaperScissors.service;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageSampler;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.imgrec.image.Image;
import org.neuroph.imgrec.image.ImageFactory;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.io.File;

/**
 * Created by Kamil S on 2016-06-07.
 */
public class ImageRecognition {
    private MultiLayerPerceptron nnet = null;

    public ImageRecognition() {
        try {
            nnet = (MultiLayerPerceptron) NeuralNetwork.load("MyImageRecognition.nnet");
        } catch (Exception e) {
            if (nnet == null){
                nnet = new MultiLayerPerceptron(TransferFunctionType.TANH, 900, 15, 1);
                nnet.getLearningRule().setMaxIterations(3000);
                learn();
                learnTest();
            }
        }

    }

    private double[] getInputFromFile(File file) {
        Image image = (Image) ImageFactory.getImage(file);
        FractionRgbData vsme;
        vsme = new FractionRgbData(ImageSampler.downSampleImage(new Dimension(15, 20), image, image.getType()));
        return vsme.getFlattenedRgbValues();
    }

    private double[] getInputFromFileName(String name) {
        return getInputFromFile(new File(name));
    }


    private void learn() {
        System.out.println("Learn start");
        DataSet dataSet = new DataSet(900, 1);
        File rockFile = new File("photo/rock");
        File paperFile = new File("photo/paper");
        File scissorFile = new File("photo/scissor");
        if(!rockFile.exists())
            rockFile.mkdirs();
        if(!paperFile.exists())
            paperFile.mkdirs();
        if(!scissorFile.exists())
            scissorFile.mkdirs();
        String[] rockPhotosName = rockFile.list();
        String[] paperPhotosName = paperFile.list();
        String[] scissorPhotosName = scissorFile.list();
        for (String rockPhotoName : rockPhotosName) {
            System.out.println("Load rock photo: " + rockPhotoName);
            double[] input = getInputFromFileName("photo/rock/" + rockPhotoName);
            dataSet.addRow(input, new double[]{Sign.ROCK.toDouble()});
        }
        for (String paperPhotoName : paperPhotosName) {
            System.out.println("Load paper photo: " + paperPhotoName);
            double[] input = getInputFromFileName("photo/paper/" + paperPhotoName);
            dataSet.addRow(input, new double[]{Sign.PAPER.toDouble()});
        }
        for (String scissorPhotoName : scissorPhotosName) {
            System.out.println("Load scissor photo: " + scissorPhotoName);
            double[] input = getInputFromFileName("photo/scissor/" + scissorPhotoName);
            dataSet.addRow(input, new double[]{Sign.SCISSORS.toDouble()});
        }
        dataSet.shuffle();
        nnet.learn(dataSet);
        System.out.println("Learn end\n");
    }

    private void learnTest() {
        System.out.println("Learn test start");
        int correctValue = 0;
        int numberOfTest = 0;
        File rockFile = new File("photo/rock");
        File paperFile = new File("photo/paper");
        File scissorFile = new File("photo/scissor");
        if(!rockFile.exists())
            rockFile.mkdirs();
        if(paperFile.exists())
            paperFile.mkdirs();
        if(!scissorFile.exists())
            scissorFile.mkdirs();
        String[] rockPhotosName = rockFile.list();
        String[] paperPhotosName = paperFile.list();
        String[] scissorPhotosName = scissorFile.list();

        for (String rockPhotoName : rockPhotosName) {
            Sign sign = recognizeImage(new File("photo/rock/" + rockPhotoName));
            System.out.println("Test rock: " + rockPhotoName + ": " + sign);
            numberOfTest++;
            if (sign == Sign.ROCK)
                correctValue++;
        }
        for (String paperPhotoName : paperPhotosName) {
            Sign sign = recognizeImage(new File("photo/paper/" + paperPhotoName));
            System.out.println("Test paper: " + paperPhotoName + ": " + sign);
            numberOfTest++;
            if (sign == Sign.PAPER)
                correctValue++;
        }
        for (String scissorPhotoName : scissorPhotosName) {
            Sign sign = recognizeImage(new File("photo/scissor/" + scissorPhotoName));
            System.out.println("Test scissor: " + scissorPhotoName + ": " + sign);
            numberOfTest++;
            if (sign == Sign.SCISSORS)
                correctValue++;
        }
        System.out.println("Learn test end: " + correctValue + "/" + numberOfTest + "\n");
    }

    public Sign recognizeImage(File image) {
        double[] input = getInputFromFile(image);
        nnet.setInput(input);
        nnet.calculate();
        double[] output = nnet.getOutput();

        if (output[0] <= -0.5)
            return Sign.ROCK;
        else if (output[0] >= 0.5)
            return Sign.SCISSORS;
        else
            return Sign.PAPER;
    }

    public void saveImage(File image, Sign sign) {
        String path = "photo/";
        if (sign == Sign.ROCK)
            path += "rock/";
        else if (sign == Sign.PAPER)
            path += "paper/";
        else if (sign == Sign.SCISSORS)
            path += "/scissor/";
        else
            return;

        image.renameTo(new File(path + image.getName()));
    }

    public void saveNetwork(){
        nnet.save("MyImageRecognition.nnet");
    }
}
