package pl.RockPaperScissors.service;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

/**
 * Created by Kamil S on 2016-06-07.
 */
public class MainGameNetwork {
    private MultiLayerPerceptron neuralNetwork;
    private DataSet trainingSet;
    private double[] actualGameRow = new double[8];
    private int round = 0;

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
    }
}
