package edu.hfut.fr.impl.training;

import java.util.ArrayList;

import edu.hfut.fr.impl.jama.Matrix;

public abstract class FeatureExtraction {

	ArrayList<Matrix> trainingSet;
	ArrayList<String> labels;
	int numOfComponents;
	Matrix meanMatrix;
	// Output
	Matrix W;
	ArrayList<TrainingMatrix> projectedTrainingSet;

	public abstract Matrix getW();

	public abstract ArrayList<TrainingMatrix> getProjectedTrainingSet();

	public abstract Matrix getMeanMatrix();

}
