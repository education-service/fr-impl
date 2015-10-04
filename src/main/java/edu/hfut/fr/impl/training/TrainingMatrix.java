package edu.hfut.fr.impl.training;

import edu.hfut.fr.impl.jama.Matrix;

public class TrainingMatrix {

	Matrix matrix;
	String label;
	double distance = 0;

	public TrainingMatrix(Matrix m, String l) {
		this.matrix = m;
		this.label = l;
	}

}
