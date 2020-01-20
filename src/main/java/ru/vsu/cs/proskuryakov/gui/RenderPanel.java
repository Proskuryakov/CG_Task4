package ru.vsu.cs.proskuryakov.gui;

import ru.vsu.cs.proskuryakov.math.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RenderPanel extends JPanel {

    private double heading;
    private double pitch;

    private double startX, startY;
    private double rotateX, rotateY;
    private boolean isPressed = false;
    private double scaling = 1;
    private List<Triangle> triangleList;


    public RenderPanel() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        super.addMouseListener(new MyMouseListener());
        super.addMouseMotionListener(new MyMouseMotionListener());
        super.addMouseWheelListener(new MyMouseWheelListener());

        triangleList = getTetrahedron();

    }

    private class MyMouseListener implements MouseListener{

        @Override
        public void mousePressed(MouseEvent e) {
            isPressed = true;
            startX = e.getX();
            startY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isPressed = false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class MyMouseMotionListener implements MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent e) {
            rotateX = e.getX() - startX;
            rotateX /= 10;
            rotateY = e.getY() - startY;
            rotateY /= 10;
            repaint();

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    private class MyMouseWheelListener implements MouseWheelListener{

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            scaling += -1 * e.getPreciseWheelRotation()/10d;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());


        if(isPressed){
            heading += Math.toRadians(rotateX);
            pitch += Math.toRadians(rotateY);

            MainPanel.headingSlider.setValue((MainPanel.headingSlider.getValue() + (int) Math.abs(rotateX)) % 360);
            MainPanel.pitchSlider.setValue((MainPanel.pitchSlider.getValue() + (int)Math.abs(rotateY)) % 360);

            rotateX = 0;
            rotateY = 0;
        }else{
            heading = Math.toRadians(MainPanel.headingSlider.getValue());
            pitch = Math.toRadians(MainPanel.pitchSlider.getValue());
        }

        Matrix4 transform = MatrixUtils.getMultiplyMatrix(MatrixUtils.getRotateYMatrix(heading), MatrixUtils.getRotateXMatrix(pitch));

        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        double[] zBuffer = new double[img.getWidth() * img.getHeight()];

        for (int q = 0; q < zBuffer.length; q++) {
            zBuffer[q] = Double.NEGATIVE_INFINITY;
        }

        for (Triangle t : triangleList) {

            Vector4 v1 = MatrixUtils.vectorOnMatrix(t.getV1(), transform);
            Vector4 v2 = MatrixUtils.vectorOnMatrix(t.getV2(), transform);
            Vector4 v3 = MatrixUtils.vectorOnMatrix(t.getV3(), transform);

            v1 = MatrixUtils.vectorOnMatrix(v1, MatrixUtils.getScalingMatrix(scaling));
            v2 = MatrixUtils.vectorOnMatrix(v2, MatrixUtils.getScalingMatrix(scaling));
            v3 = MatrixUtils.vectorOnMatrix(v3, MatrixUtils.getScalingMatrix(scaling));

            double angleCos = Math.abs(getNormal(v1, v2, v3).getZ());

            transfer(v1, v2, v3);

            int minX = (int) Math.max(0, Math.ceil(Math.min(v1.getX(), Math.min(v2.getX(), v3.getX()))));
            int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.getX(), Math.max(v2.getX(), v3.getX()))));
            int minY = (int) Math.max(0, Math.ceil(Math.min(v1.getY(), Math.min(v2.getY(), v3.getY()))));
            int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.getY(), Math.max(v2.getY(), v3.getY()))));

            double triangleArea =
                    (v1.getY() - v3.getY()) * (v2.getX() - v3.getX()) + (v2.getY() - v3.getY()) * (v3.getX() - v1.getX());

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    double b1 = ((y - v3.getY()) * (v2.getX() - v3.getX()) + (v2.getY() - v3.getY()) * (v3.getX() - x)) / triangleArea;
                    double b2 = ((y - v1.getY()) * (v3.getX() - v1.getX()) + (v3.getY() - v1.getY()) * (v1.getX() - x)) / triangleArea;
                    double b3 = ((y - v2.getY()) * (v1.getX() - v2.getX()) + (v1.getY() - v2.getY()) * (v2.getX() - x)) / triangleArea;

                    if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {

                        double depth = b1 * v1.getZ() + b2 * v2.getZ() + b3 * v3.getZ();
                        int zIndex = y * img.getWidth() + x;
                        if (zBuffer[zIndex] < depth) {
                            img.setRGB(x, y, getNewColor(t.getColor(), angleCos).getRGB());
                            zBuffer[zIndex] = depth;
                        }

                    }
                }
            }
        }

        g2.drawImage(img, 0, 0, null);

    }

    private void transfer(Vector4 v1, Vector4 v2, Vector4 v3){
        double halfWidth = getWidth() / 2, halfHeight = getHeight() / 2;
        v1.setX(v1.getX() + halfWidth);
        v1.setY(v1.getY() + halfHeight);
        v2.setX(v2.getX() + halfWidth);
        v2.setY(v2.getY() + halfHeight);
        v3.setX(v3.getX() + halfWidth);
        v3.setY(v3.getY() + halfHeight);
    }

    private Vector4 getNormal(Vector4 v1, Vector4 v2, Vector4 v3){

        //чтобы найти нормаль, нужно нать два ветора в одной плоскости (в данном слчае две стороны треугольника)
        Vector4 ab = new Vector4(v2.getX() - v1.getX(), v2.getY() - v1.getY(), v2.getZ() - v1.getZ());
        Vector4 ac = new Vector4(v3.getX() - v1.getX(), v3.getY() - v1.getY(), v3.getZ() - v1.getZ());

        Vector4 norm = new Vector4(ab.getY() * ac.getZ() - ab.getZ() * ac.getY(),
                ab.getZ() * ac.getX() - ab.getX() * ac.getZ(),
                ab.getX() * ac.getY() - ab.getY() * ac.getX());
        //нормализуем
        double normalLength =
                Math.sqrt(norm.getX() * norm.getX() + norm.getY() * norm.getY() + norm.getZ() * norm.getZ());

        norm.setX( norm.getX() / normalLength);
        norm.setY( norm.getY() / normalLength);
        norm.setZ( norm.getZ() / normalLength);

        return norm;
    }

    private Color getNewColor(Color color, double shade) {
//        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
//        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
//        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;
//
//        int red = (int) Math.pow(redLinear, 1/2.4);
//        int green = (int) Math.pow(greenLinear, 1/2.4);
//        int blue = (int) Math.pow(blueLinear, 1/2.4);


        int red = (int) (color.getRed() * shade);
        int green = (int)(color.getGreen() * shade);
        int blue = (int) (color.getBlue() * shade);

        return new Color(red, green, blue);
    }

    private List<Triangle> getTetrahedron(){

        List<Triangle> tris = new ArrayList<>();

        tris.add(new Triangle(new Vector4(100, 100, 100),
                new Vector4(-100, -100, 100),
                new Vector4(-100, 100, -100),
                Color.WHITE));

        tris.add(new Triangle(new Vector4(100, 100, 100),
                new Vector4(-100, -100, 100),
                new Vector4(100, -100, -100),
                Color.RED));

        tris.add(new Triangle(new Vector4(-100, 100, -100),
                new Vector4(100, -100, -100),
                new Vector4(100, 100, 100),
                Color.GREEN));

        tris.add(new Triangle(new Vector4(-100, 100, -100),
                new Vector4(100, -100, -100),
                new Vector4(-100, -100, 100),
                Color.BLUE));

        return tris;
    }

    private List<Triangle> getCube(){
        List<Triangle> triangleList = new ArrayList<>();
        //верхние
        triangleList.add(new Triangle(new Vector4 (50, 50, 50), new Vector4 (50, -50, 50),
                        new Vector4 (-50, 50, 50), Color.BLUE));

        triangleList.add(new Triangle(new Vector4 (-50, -50, 50), new Vector4 (50, -50, 50),
                        new Vector4 (-50, 50, 50), Color.BLUE));

        //нижние
        triangleList.add(new Triangle(new Vector4 (50, 50, -50), new Vector4 (50, -50, -50),
                        new Vector4 (-50, 50, -50), Color.RED));

        triangleList.add(new Triangle(new Vector4 (-50, -50, -50), new Vector4 (50, -50, -50),
                        new Vector4 (-50, 50, -50), Color.RED));

        //бок лево
        triangleList.add(new Triangle(new Vector4 (-50, -50, 50), new Vector4 (-50, 50, 50),
                        new Vector4 (-50, -50, -50), Color.GREEN));

        triangleList.add(new Triangle(new Vector4 (-50, -50, -50), new Vector4 (-50, 50, -50),
                        new Vector4 (-50, 50, 50), Color.GREEN));

        //бок право
        triangleList.add(new Triangle(new Vector4 (50, 50, 50), new Vector4 (50, -50, 50),
                        new Vector4 (50, -50, -50), Color.WHITE));

        triangleList.add(new Triangle(new Vector4 (50, -50, -50), new Vector4 (50, 50, -50),
                        new Vector4 (50, 50, 50), Color.WHITE));

        //бок верх
        triangleList.add(new Triangle(new Vector4 (-50, 50, 50), new Vector4 (50, 50, 50),
                        new Vector4 (-50, 50, -50), Color.YELLOW));

        triangleList.add(new Triangle(new Vector4 (-50, 50, -50), new Vector4 (50, 50, -50),
                        new Vector4 (50, 50, 50), Color.YELLOW));

        //бок низ
        triangleList.add(new Triangle(new Vector4 (-50, -50, 50), new Vector4 (50, -50, 50),
                        new Vector4 (-50, -50, -50), Color.MAGENTA));

        triangleList.add(new Triangle(new Vector4 (-50, -50, -50), new Vector4 (50, -50, -50),
                        new Vector4 (50, -50, 50), Color.MAGENTA));


        return triangleList;
    }

    private List<Triangle> getOctahedron(){
        List<Triangle> triangleList = new ArrayList<>();

        double vertex = 50*Math.sqrt(2);

        triangleList.add(new Triangle(new Vector4 (-50, -50, 0),
                new Vector4 (50, -50, 0),
                new Vector4 (0, 0, vertex),
                Color.WHITE));

        triangleList.add(new Triangle(new Vector4 (-50, -50, 0),
                new Vector4 (-50, 50, 0),
                new Vector4 (0, 0, vertex),
                Color.BLUE));

        triangleList.add(new Triangle(new Vector4 (50, -50, 0),
                new Vector4 (50, 50, 0),
                new Vector4 (0, 0, vertex),
                Color.GREEN));

        triangleList.add(new Triangle(new Vector4 (-50, 50, 0),
                new Vector4 (50, 50, 0),
                new Vector4 (0, 0, vertex),
                Color.YELLOW));


        triangleList.add(new Triangle(new Vector4 (-50, -50, 0),
                new Vector4 (50, -50, 0),
                new Vector4 (0, 0, -vertex),
                Color.orange));

        triangleList.add(new Triangle(new Vector4 (-50, 50, 0),
                new Vector4 (50, 50, 0),
                new Vector4 (0, 0, -vertex),
                Color.PINK));

        triangleList.add(new Triangle(new Vector4 (-50, -50, 0),
                new Vector4 (-50, 50, 0),
                new Vector4 (0, 0, -vertex),
                Color.MAGENTA));

        triangleList.add(new Triangle(new Vector4 (50, -50, 0),
                new Vector4 (50, 50, 0),
                new Vector4 (0, 0, -vertex),
                Color.CYAN));


        return triangleList;
    }

    private List<Triangle> getIcosahedron(){

        double radius = 100d;
        //перевод в радианы
        double magicAngle = Math.PI * 26.565d/180;
        double segmentAngle = Math.PI * 72 / 180;
        double currentAngle = 0d;

        Vector4 [] v = new Vector4 [12];

        v[0] = new Vector4 (0, radius, 0);
        v[11] = new Vector4 (0, -radius, 0);

        for (int i = 1; i < 6; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(magicAngle),
                    radius * Math.sin(magicAngle),
                    radius * Math.cos(currentAngle) * Math.cos(magicAngle));
            currentAngle += segmentAngle;
        }

        currentAngle = Math.PI*36/180;

        for (int i=6; i<11; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(-magicAngle),
                    radius * Math.sin(-magicAngle),
                    radius * Math.cos(currentAngle) * Math.cos(-magicAngle));
            currentAngle += segmentAngle;
        }

        List<Triangle> triangleList = new ArrayList<>();

        triangleList.add(new Triangle(v[0], v[1], v[2], new Color(0x07CA00)));
        triangleList.add(new Triangle(v[0], v[2], v[3], new Color(241, 255, 0)));
        triangleList.add(new Triangle(v[0], v[3], v[4], new Color(255, 6, 0)));
        triangleList.add(new Triangle(v[0], v[4], v[5], new Color(255, 0, 205)));
        triangleList.add(new Triangle(v[0], v[5], v[1], new Color(0, 4, 255)));

        triangleList.add(new Triangle(v[11], v[7], v[6], new Color(0, 255, 252)));
        triangleList.add(new Triangle(v[11], v[8], v[7], new Color(154, 0, 150)));
        triangleList.add(new Triangle(v[11], v[9], v[8], new Color(0, 18, 134)));
        triangleList.add(new Triangle(v[11], v[10], v[9], new Color(0, 127, 5)));
        triangleList.add(new Triangle(v[11], v[6], v[10], new Color(0, 126, 132)));

        triangleList.add(new Triangle(v[2], v[1], v[6], new Color(147, 0, 179)));
        triangleList.add(new Triangle(v[3], v[2], v[7], new Color(160, 192, 212)));
        triangleList.add(new Triangle(v[4], v[3], v[8], new Color(186, 136, 74)));
        triangleList.add(new Triangle(v[5], v[4], v[9], new Color(24, 200, 113)));
        triangleList.add(new Triangle(v[1], v[5], v[10], new Color(187, 34, 50)));

        triangleList.add(new Triangle(v[6], v[7], v[2], new Color(207, 240, 255)));
        triangleList.add(new Triangle(v[7], v[8], v[3], new Color(48, 154, 127)));
        triangleList.add(new Triangle(v[8], v[9], v[4], new Color(0, 191, 136)));
        triangleList.add(new Triangle(v[9], v[10], v[5], new Color(199, 137, 117)));
        triangleList.add(new Triangle(v[10], v[6], v[1], new Color(235, 61, 114)));

        return triangleList;

    }

    private List<Triangle> getDodecahedron(){

        double radius = 100d;
        //перевод в радианы

        double upperLayerAngle = Math.toRadians(52.625776d);
        double bottomLayerAngle = Math.toRadians(10.812576d);

        double segmentAngle = Math.toRadians(72);
        double currentAngle = 0d;

        Vector4 [] v = new Vector4 [20];

        for (int i = 0; i < 5; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(upperLayerAngle),
                    radius * Math.sin(upperLayerAngle),
                    radius * Math.cos(currentAngle) * Math.cos(upperLayerAngle));
            currentAngle += segmentAngle;
        }
        currentAngle = 0d;
        for (int i = 5; i < 10; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(bottomLayerAngle),
                    radius * Math.sin(bottomLayerAngle),
                    radius * Math.cos(currentAngle) * Math.cos(bottomLayerAngle));
            currentAngle += segmentAngle;
        }

        currentAngle = Math.toRadians(36);

        for (int i=10; i < 15; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(-bottomLayerAngle),
                    radius * Math.sin(-bottomLayerAngle),
                    radius * Math.cos(currentAngle) * Math.cos(-bottomLayerAngle));
            currentAngle += segmentAngle;
        }
        currentAngle = Math.toRadians(36);
        for (int i=15; i < 20; i++)
        {
            v[i] = new Vector4 (radius * Math.sin(currentAngle) * Math.cos(-upperLayerAngle),
                    radius * Math.sin(-upperLayerAngle),
                    radius * Math.cos(currentAngle) * Math.cos(-upperLayerAngle));
            currentAngle += segmentAngle;
        }

        List<Triangle> triangleList = new ArrayList<>();

        addPentagon(triangleList, v[0], v[1], v[4], v[2], v[3], new Color(255, 255, 255));

        addPentagon(triangleList, v[0], v[1], v[5], v[6], v[10], new Color(255, 255, 0));

        addPentagon(triangleList, v[1], v[2], v[6], v[7], v[11], new Color(0, 0, 200));

        addPentagon(triangleList, v[2], v[3], v[7], v[8], v[12], new Color(255, 0, 0));

        addPentagon(triangleList, v[3], v[4], v[8], v[9], v[13], new Color(0, 177, 0));

        addPentagon(triangleList, v[4], v[0], v[9], v[5], v[14], new Color(166, 0, 168));

        addPentagon(triangleList, v[15], v[16], v[19], v[17], v[18], new Color(142, 142, 142));

        addPentagon(triangleList, v[16], v[15], v[11], v[10], v[6], new Color(52, 255, 46));

        addPentagon(triangleList, v[17], v[16], v[12], v[11], v[7], new Color(255, 197, 225));

        addPentagon(triangleList, v[18], v[17], v[13], v[12], v[8], new Color(255, 254, 147));

        addPentagon(triangleList, v[19], v[18], v[14], v[13], v[9], new Color(0, 255, 245));

        addPentagon(triangleList, v[15], v[19], v[10], v[14], v[5], new Color(255, 155, 0));

        return triangleList;

    }

    private void addPentagon(List<Triangle> triangleList, Vector4 v1, Vector4 v2, Vector4 v3, Vector4 v4, Vector4 v5, Color color){

        triangleList.add(new Triangle(v1, v2, v3, color));
        triangleList.add(new Triangle(v3, v4, v5, color));
        triangleList.add(new Triangle(v2, v3, v4, color));

    }

    public void tetrahedronButton(){
        triangleList = getTetrahedron();
        scaling = 1;
        repaint();
    }

    public void cubeButton(){
        triangleList = getCube();
        scaling = 1;
        repaint();
    }

    public void octahedronButton(){
        triangleList = getOctahedron();
        scaling = 1;
        repaint();
    }

    public void icosahedronButton(){
        triangleList = getIcosahedron();
        scaling = 1;
        repaint();
    }

    public void dodecahedronButton(){
        triangleList = getDodecahedron();
        scaling = 1;
        repaint();
    }


}
