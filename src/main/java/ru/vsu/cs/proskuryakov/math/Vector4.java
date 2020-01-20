package ru.vsu.cs.proskuryakov.math;

public class Vector4 {

    private double x;
    private double y;
    private double z;
    private double normal;

    public Vector4() {
    }

    public Vector4(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.normal = 1;
    }

    public Vector4(double x, double y, double z, double normal) {
        if(normal == 1){
            this.x = x;
            this.y = y;
            this.z = z;
            this.normal = normal;
        }else{
            this.x = x/normal;
            this.y = y/normal;
            this.z = z/normal;
            this.normal = 1;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getNormal() {
        return normal;
    }

    public void setNormal(double normal) {
        this.normal = normal;
    }

    public boolean isNormal(){
        return normal == 1;
    }

}
