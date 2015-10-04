package edu.hfut.fr.impl.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import edu.hfut.fr.impl.jama.Matrix;
import edu.hfut.fr.impl.training.CosineDissimilarity;
import edu.hfut.fr.impl.training.EuclideanDistance;
import edu.hfut.fr.impl.training.FeatureExtraction;
import edu.hfut.fr.impl.training.FileManager;
import edu.hfut.fr.impl.training.KNN;
import edu.hfut.fr.impl.training.L1Distance;
import edu.hfut.fr.impl.training.LDA;
import edu.hfut.fr.impl.training.LPP;
import edu.hfut.fr.impl.training.Metric;
import edu.hfut.fr.impl.training.PCA;
import edu.hfut.fr.impl.training.TrainingMatrix;

public class PerformanceTest {
	public static void main(String args[]) {
		//Test Different Methods
		//Notice that the second parameter which is a measurement of energy percentage does not apply to LDA and LPP
		test(2, 101, 0, 7, 2);
		//		test(2,60,1,3,2);
		//		test(2,60,2,3,2);
	}

	/*metricType:
	 * 	0: CosineDissimilarity
	 * 	1: L1Distance
	 * 	2: EuclideanDistance
	 *
	 * energyPercentage:
	 *  PCA: components = samples * energyPercentage
	 *  LDA: components = (c-1) *energyPercentage
	 *  LLP: components = (c-1) *energyPercentage
	 *
	 * featureExtractionMode
	 * 	0: PCA
	 *	1: LDA
	 * 	2: LLP
	 *
	 * trainNums: how many numbers in 1..10 are assigned to be training faces
	 * for each class, randomly generate the set
	 *
	 * knn_k: number of K for KNN algorithm
	 *
	 * */
	static double test(int metricType, int componentsRetained, int featureExtractionMode, int trainNums, int knn_k) {
		//determine which metric is used
		//metric
		Metric metric = null;
		if (metricType == 0)
			metric = new CosineDissimilarity();
		else if (metricType == 1)
			metric = new L1Distance();
		else if (metricType == 2)
			metric = new EuclideanDistance();

		assert metric != null : "metricType is wrong!";

		//set expectedComponents according to energyPercentage
		//componentsRetained
		//		int trainingSize = trainNums * 40;
		//		int componentsRetained = 0;
		//		if(featureExtractionMode == 0)
		//			componentsRetained = (int) (trainingSize * energyPercentage);
		//		else if(featureExtractionMode == 1)
		//			componentsRetained = (int) ((40 -1) * energyPercentage);
		//		else if(featureExtractionMode == 2)
		//			componentsRetained = (int) ((40 -1) * energyPercentage);

		//set trainSet and testSet
		HashMap<String, ArrayList<Integer>> trainMap = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> testMap = new HashMap<String, ArrayList<Integer>>();
		for (int i = 1; i <= 40; i++) {
			String label = "s" + i;
			ArrayList<Integer> train = generateTrainNums(trainNums);
			ArrayList<Integer> test = generateTestNums(train);
			trainMap.put(label, train);
			testMap.put(label, test);
		}

		//trainingSet & respective labels
		ArrayList<Matrix> trainingSet = new ArrayList<Matrix>();
		ArrayList<String> labels = new ArrayList<String>();

		Set<String> labelSet = trainMap.keySet();
		Iterator<String> it = labelSet.iterator();
		while (it.hasNext()) {
			String label = it.next();
			ArrayList<Integer> cases = trainMap.get(label);
			for (int i = 0; i < cases.size(); i++) {
				String filePath = "faces/" + label + "/" + cases.get(i) + ".pgm";
				Matrix temp;
				try {
					temp = FileManager.convertPGMtoMatrix(filePath);
					trainingSet.add(vectorize(temp));
					labels.add(label);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		//testingSet & respective true labels
		ArrayList<Matrix> testingSet = new ArrayList<Matrix>();
		ArrayList<String> trueLabels = new ArrayList<String>();

		labelSet = testMap.keySet();
		it = labelSet.iterator();
		while (it.hasNext()) {
			String label = it.next();
			ArrayList<Integer> cases = testMap.get(label);
			for (int i = 0; i < cases.size(); i++) {
				String filePath = "faces/" + label + "/" + cases.get(i) + ".pgm";
				Matrix temp;
				try {
					temp = FileManager.convertPGMtoMatrix(filePath);
					testingSet.add(vectorize(temp));
					trueLabels.add(label);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		//set featureExtraction
		try {
			FeatureExtraction fe = null;
			if (featureExtractionMode == 0)
				fe = new PCA(trainingSet, labels, componentsRetained);
			else if (featureExtractionMode == 1)
				fe = new LDA(trainingSet, labels, componentsRetained);
			else if (featureExtractionMode == 2)
				fe = new LPP(trainingSet, labels, componentsRetained);

			FileManager.convertMatricetoImage(fe.getW(), featureExtractionMode);

			//PCA Reconstruction
			//
			//			Matrix hhMatrix = ((PCA) fe).reconstruct(50, 60);
			//			FileManager.convertToImage(hhMatrix, 60);
			//
			//			hhMatrix = ((PCA) fe).reconstruct(50, 40);
			//			FileManager.convertToImage(hhMatrix, 40);
			//
			//			hhMatrix = ((PCA) fe).reconstruct(50, 20);
			//			FileManager.convertToImage(hhMatrix, 20);
			//
			//			hhMatrix = ((PCA) fe).reconstruct(50, 10);
			//			FileManager.convertToImage(hhMatrix, 10);
			//
			//			hhMatrix = ((PCA) fe).reconstruct(50, 6);
			//			FileManager.convertToImage(hhMatrix, 6);
			//
			//			hhMatrix = ((PCA) fe).reconstruct(50, 2);
			//			FileManager.convertToImage(hhMatrix, 2);
			//
			//			hhMatrix = ((PCA)fe).getTrainingSet().get(50);

			//use test cases to validate
			//testingSet   trueLables
			ArrayList<TrainingMatrix> projectedTrainingSet = fe.getProjectedTrainingSet();
			int accurateNum = 0;
			for (int i = 0; i < testingSet.size(); i++) {
				Matrix testCase = fe.getW().transpose().times(testingSet.get(i).minus(fe.getMeanMatrix()));
				String result = KNN.assignLabel(projectedTrainingSet.toArray(new TrainingMatrix[0]), testCase, knn_k,
						metric);

				if (result == trueLabels.get(i))
					accurateNum++;
			}
			double accuracy = accurateNum / (double) testingSet.size();
			System.out.println("The accuracy is " + accuracy);
			return accuracy;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return -1;
	}

	static ArrayList<Integer> generateTrainNums(int trainNum) {
		Random random = new Random();
		ArrayList<Integer> result = new ArrayList<Integer>();

		while (result.size() < trainNum) {
			int temp = random.nextInt(10) + 1;
			while (result.contains(temp)) {
				temp = random.nextInt(10) + 1;
			}
			result.add(temp);
		}

		return result;
	}

	static ArrayList<Integer> generateTestNums(ArrayList<Integer> trainSet) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 1; i <= 10; i++) {
			if (!trainSet.contains(i))
				result.add(i);
		}
		return result;
	}

	//Convert a m by n matrix into a m*n by 1 matrix
	static Matrix vectorize(Matrix input) {
		int m = input.getRowDimension();
		int n = input.getColumnDimension();

		Matrix result = new Matrix(m * n, 1);
		for (int p = 0; p < n; p++) {
			for (int q = 0; q < m; q++) {
				result.set(p * m + q, 0, input.get(q, p));
			}
		}
		return result;
	}
}
