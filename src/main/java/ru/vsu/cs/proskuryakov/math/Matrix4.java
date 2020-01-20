package ru.vsu.cs.proskuryakov.math;

import java.util.Arrays;

public class Matrix4 {

    private double[][] value;

    public Matrix4(double[][] value) {
        this.value = value;
    }

    public void multiply(Matrix4 matrix){
        this.value = MatrixUtils.getResultMultipluMatrix(value, matrix.getValue());
    }


    public double[][] getValue() {
        return value;
    }

    public void setValue(double[][] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < value.length; i++){
            for (int j = 0; j < value[0].length; j++) {
                sb.append(value[i][j]);
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
