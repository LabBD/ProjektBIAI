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

public class ImageRecognitionSample {

    public static  void print(double[] output){
       System.out.println("Kamien: "+output[0]+" papier: "+output[1]+ " nozyce: "+output[2]);
    }

    public static String[] generateNames(String name, int amount){
        String[] names = new String[amount];
        for(int i=1;i<=amount;i++){
            names[i-1]= name +" ("+i+").jpg";
        }
        return names;
    }

    public static void learn(String[] names, MultiLayerPerceptron nnet) {
        DataSet dataSet = new DataSet(900, 3);
        int i = 1;
        for (String name : names) {
            double[] input = getInput(name);
            if (i <= 3)
                dataSet.addRow(input, new double[]{0.0,1.0,0.0});
            else if (i <= 6)
                dataSet.addRow(input, new double[]{0.0,0.0,1.0});
//            else
//                dataSet.addRow(input, new double[]{1.0});
            i++;
            System.out.println("Load " + name);
        }

        nnet.learn(dataSet);

    }

    public static double[] getInput(String name) {
        Image image = (Image) ImageFactory.getImage(new File(name));

        FractionRgbData vsme;
        vsme = new FractionRgbData(ImageSampler.downSampleImage(new Dimension(15, 20), image, image.getType()));
        return vsme.getFlattenedRgbValues();
    }

    public static void main(String[] args) {
        MultiLayerPerceptron nnet = null;
        try{
            nnet = (MultiLayerPerceptron) NeuralNetwork.load("MyImageRecognition.nnet");
        }catch (Exception e){
            if (nnet == null)
                nnet = new MultiLayerPerceptron(TransferFunctionType.TANH, 900, 12, 3);

        }
        nnet.getLearningRule().setMaxIterations(10000);


        try {
//            learn(new String[]{"test (1).jpg", "test (2).jpg", "test (3).jpg", "test (4).jpg", "test (5).jpg", "test (6).jpg",}, nnet);
            learn(generateNames("test",6),nnet);
            nnet.setInput(getInput("test (2).jpg"));
            nnet.calculate();
            print(nnet.getOutput());

            nnet.setInput(getInput("test (4).jpg"));
            nnet.calculate();
            print(nnet.getOutput());

            nnet.setInput(getInput("test.jpg"));
            nnet.calculate();
            print(nnet.getOutput());

            nnet.setInput(getInput("1.jpg"));
            nnet.calculate();
            print(nnet.getOutput());


            nnet.save("MyImageRecognition.nnet");
            System.out.println("done");
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}