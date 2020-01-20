package ru.vsu.cs.proskuryakov.math;

import java.util.ArrayList;
import java.util.List;

public class MatrixUtils {

    public static Matrix4 getMultiplyMatrix(Matrix4 matrix1, Matrix4 matrix2){

        double[][] matrix1Value = matrix1.getValue();
        double[][] matrix2Value = matrix2.getValue();

        if(matrix1Value[0].length != matrix2Value.length) return null;

        return new Matrix4(getResultMultipluMatrix(matrix1Value, matrix2Value));

    }

    public static double[][] getResultMultipluMatrix(double[][] value, double[][] input){
        double[][] result = new double[4][4];

        for(int i = 0; i < value.length; i++){

            for(int j = 0; j < value[0].length; j++){
                double sum = 0;
                for (int k = 0; k < value[0].length; k++) {
                    sum += value[i][k] * input[k][j];
                }
                result[i][j] = sum;
            }

        }
        return result;
    }

    public static Matrix4 getRotateYMatrix(double angle){
        return new Matrix4(new double[][]
                {{Math.cos(angle), 0, Math.sin(angle), 0},
                        {0, 1, 0, 0},
                        {-Math.sin(angle), 0, Math.cos(angle), 0},
                        {0,0,0,1}});
    }

    public static Matrix4 getRotateXMatrix(double angle){
        return new Matrix4(new double[][]
                {{1, 0, 0, 0},
                        {0, Math.cos(angle), Math.sin(angle), 0},
                        {0, -Math.sin(angle), Math.cos(angle), 0},
                        {0,0,0,1}});
    }

    public static Matrix4 getRotateZMatrix(double angle){
        return new Matrix4(new double[][]
                {{Math.cos(angle), -Math.sin(angle), 0, 0},
                        {Math.sin(angle), Math.cos(angle), 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}});
    }

    public static Matrix4 getScalingMatrix(double scaling){
        return new Matrix4(new double[][]
                {{scaling, 0, 0, 0},
                {0, scaling, 0, 0},
                {0, 0, scaling, 0},
                {0, 0, 0, 1}});
    }

    public static Matrix4 getProjectionZMatrix(){
        return new Matrix4(new double[][]
                {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 1}});
    }

    public static List<Vector4> listVectorOnMatrix(List<Vector4> vectorList, Matrix4 matrix){
        List<Vector4> newList = new ArrayList<>();
        for(Vector4 v: vectorList){
            newList.add(vectorOnMatrix(v, matrix));
        }
        return newList;
    }

    public static Vector4 vectorOnMatrix(Vector4 vector, Matrix4 matrix){
        double[][] value = matrix.getValue();

        double x = vector.getX()*value[0][0] + vector.getY()*value[1][0] + vector.getZ()*value[2][0] + vector.getNormal()*value[3][0];
        double y = vector.getX()*value[0][1] + vector.getY()*value[1][1] + vector.getZ()*value[2][1] + vector.getNormal()*value[3][1];
        double z = vector.getX()*value[0][2] + vector.getY()*value[1][2] + vector.getZ()*value[2][2] + vector.getNormal()*value[3][2];
        double normal = vector.getX()*value[0][3] + vector.getY()*value[1][3] + vector.getZ()*value[2][3] + vector.getNormal()*value[3][3];

        return new Vector4(x,y,z,normal);
    }

}
