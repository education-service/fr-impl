package edu.hfut.fr.impl.training;

import edu.hfut.fr.impl.jama.Matrix;

public interface Metric {

	double getDistance(Matrix a, Matrix b);

}
