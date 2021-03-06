package pl.RockPaperScissors.service;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Kamil S on 2016-06-07.
 */
public class MainGameNetwork {
    private MultiLayerPerceptron neuralNetwork;
    private DataSet trainingSet;
    private double[] actualGameRow = new double[8];
    private int round = 0;
    private HashMap<String, Double> playersIdMap;
    private HashMap<String, Double> playersGameIdMap;
    public static String nickname;

    public MainGameNetwork() {
        try {
            trainingSet = DataSet.load("dataset");
        } catch (NeurophException exception) {
            trainingSet = new DataSet(8, 1);
            trainingSet.setColumnNames(
                    new String[]{"PlayerId", "GameId", "Game1", "Game2", "Game3", "Game4", "Game5", "Game6"});

            trainingSet.addRow(new double[]{0, 0, 0, 0, 0, 0, 0, 0}, new double[]{0.0});

        }

        neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.TANH, 8, 8, 1);
        neuralNetwork.getLearningRule().setMaxIterations(10000);
        learn();
        playersIdMap = loadMap("playerIdMap.ser");
        Double playerId = playersIdMap.get(nickname);
        if (playerId == null) {
            playerId = Double.valueOf(playersIdMap.size());
            playersIdMap.put(nickname, playerId);
        }
        setPlayerId(playerId);

        playersGameIdMap = loadMap("playersGameIdMap.ser");
        Double gameId = playersGameIdMap.get(nickname);
        if (gameId == null) {
            gameId = 0.0;
        }
        playersGameIdMap.put(nickname, gameId + 1.0);
        setGameId(gameId);


    }

    public Sign networkMove() {
        neuralNetwork.setInput(actualGameRow);
        neuralNetwork.calculate();
        double[] networkOutput = neuralNetwork.getOutput();
        return Sign.get(networkOutput[0]);
    }

    public void setPlayerMove(Sign playerMove) {
        if (trainingSet.size() < 18)
            learn();
        actualGameRow[round + 2] = playerMove.toDouble() + 2;
        trainingSet.addRow(new double[]{actualGameRow[0], actualGameRow[1], actualGameRow[2], actualGameRow[3], actualGameRow[4],
                actualGameRow[5], actualGameRow[6], actualGameRow[7]}, new double[]{playerMove.counterSign().toDouble()});
        round++;

    }

    public void showNeuralNetworkTest() {

        for (DataSetRow dataRow : trainingSet.getRows()) {

            neuralNetwork.setInput(dataRow.getInput());
            neuralNetwork.calculate();
            double[] networkOutput = neuralNetwork.getOutput();
            System.out.print("Input: " + Arrays.toString(dataRow.getInput()) + " Desired output: " + Arrays.toString(dataRow.getDesiredOutput()));
            System.out.println(" Output: " + Arrays.toString(networkOutput));

        }

    }

    public void setPlayerId(double playerId) {
        actualGameRow[0] = playerId;
    }

    public void setGameId(double gameId) {
        actualGameRow[1] = gameId;
    }

    private void learn() {
        neuralNetwork.learn(trainingSet);
    }

    public void save() {
        trainingSet.save("dataset");
        saveMap(playersIdMap, "playerIdMap.ser");
        saveMap(playersGameIdMap, "playersGameIdMap.ser");
    }

    private HashMap<String, Double> loadMap(String hashMapFileName) {
        HashMap<String, Double> map = null;
        try {

            FileInputStream fis = new FileInputStream(hashMapFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            if (map == null)
                map = new HashMap<>();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();

        }
        return map;
    }

    public void saveMap(HashMap<String, Double> map, String hashMapFileName) {
        try {
            FileOutputStream fos =
                    new FileOutputStream(hashMapFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
