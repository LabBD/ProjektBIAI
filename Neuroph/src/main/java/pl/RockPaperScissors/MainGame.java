package pl.RockPaperScissors;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import view.GameView;

public class MainGame {

	private MultiLayerPerceptron neuralNetwork;
	private DataSet trainingSet;
	private GameView gameView = new GameView();

	public MainGame() {

		try {
			trainingSet = DataSet.load("dataset");
		} catch (NeurophException exception) {
			trainingSet = new DataSet(8, 1);
			trainingSet.setColumnNames(
					new String[] { "PlayerId", "GameId", "Game1", "Game2", "Game3", "Game4", "Game5", "Game6" });

			trainingSet.addRow(new double[] { 0, 0, 0, 0, 0, 0, 0, 0 }, new double[] { 1.0 });
			trainingSet.save("dataset");

		}

		neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.TANH, 8, 8, 1);
		neuralNetwork.getLearningRule().setMaxIterations(10000);
		neuralNetwork.learn(trainingSet);

		showNeuralNetworkTest();
	}

	public void startGame() {
		double[] userData = gameView.colectUserData();
		double output = 0;
		int numberOfNetworkWin = 0;
		int numberOfNetworkTie = 0;
		for (int i = 2; i < 8; i++) {
			neuralNetwork.setInput(userData);
			neuralNetwork.calculate();
			double[] networkOutput = neuralNetwork.getOutput();
			Double selectedNumber =  gameView.takeNumber();
			System.out.println(" Output number : " + Arrays.toString(networkOutput));
			if (selectedNumber == 1) {
				output = 0.0;
			} else if (selectedNumber == 2) {
				output = 1.0;
			} else if (selectedNumber == 3) {
				output = -1.0;
			}
			
			if(Math.abs(networkOutput[0]-output)<0.5){
				System.out.println("Siec wygrala ");
				numberOfNetworkWin++;
			}if(Math.abs(networkOutput[0]+2-selectedNumber)<0.5){
				System.out.println("Remis ");
				numberOfNetworkTie++;
			}if(Math.abs(networkOutput[0]+2-selectedNumber)>0.5 && Math.abs(networkOutput[0]-output)>0.5)
			{
				System.out.println("Wygrales ");
			}
				
			
			System.out.println(Math.abs(networkOutput[0]-output));
			trainingSet.addRow(new double[] { userData[0], userData[1], userData[2], userData[3], userData[4],
					userData[5], userData[6], userData[7] }, new double[] { output });
			neuralNetwork.learn(trainingSet);
//			showNeuralNetworkTest();
			userData[i] = selectedNumber;
		}
		System.out.println("Koniec gry!");
		System.out.println("Siec wygrala " +numberOfNetworkWin);
		System.out.println("Siec zremisowala " +numberOfNetworkTie);
		trainingSet.addRow(new double[] { userData[0], userData[1], userData[2], userData[3], userData[4],
				userData[5], userData[6], userData[7] }, new double[] { output });
		neuralNetwork.learn(trainingSet);
		showNeuralNetworkTest();
		trainingSet.save("dataset");
	}

	public void showNeuralNetworkTest() {

		for (DataSetRow dataRow : trainingSet.getRows()) {

			neuralNetwork.setInput(dataRow.getInput());
			neuralNetwork.calculate();
			double[] networkOutput = neuralNetwork.getOutput();
			System.out.print("Input: " + Arrays.toString(dataRow.getInput())+ " Desired output: "+Arrays.toString(dataRow.getDesiredOutput()));
			System.out.println(" Output: " + Arrays.toString(networkOutput));

		}

	}

	public static void main(String[] args) {
		MainGame mainGame = new MainGame();
		mainGame.startGame();
		System.out.println("Wszystko spoko !");
	}

}
