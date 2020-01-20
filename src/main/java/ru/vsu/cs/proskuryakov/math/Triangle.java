package ru.vsu.cs.proskuryakov.math;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Triangle {

    private Vector4 v1;
    private Vector4 v2;
    private Vector4 v3;
    private Color color;

    public Triangle(Vector4 v1, Vector4 v2, Vector4 v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    public List<Vector4> getVertexList(){
        List<Vector4> list = new ArrayList<>();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        return list;
    }

    public Vector4 getV1() {
        return v1;
    }

    public void setV1(Vector4 v1) {
        this.v1 = v1;
    }

    public Vector4 getV2() {
        return v2;
    }

    public void setV2(Vector4 v2) {
        this.v2 = v2;
    }

    public Vector4 getV3() {
        return v3;
    }

    public void setV3(Vector4 v3) {
        this.v3 = v3;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
