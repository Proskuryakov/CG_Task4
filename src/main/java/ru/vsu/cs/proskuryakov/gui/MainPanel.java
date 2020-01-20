package ru.vsu.cs.proskuryakov.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainPanel {

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        JFrame frame = new JFrame();
        Container mainPanel = frame.getContentPane();
        mainPanel.setLayout(new BorderLayout());

        initSlider();
        renderPanel = new RenderPanel();

        mainPanel.add(getPanelWithButton(), BorderLayout.NORTH);
        mainPanel.add(headingSlider, BorderLayout.SOUTH);
        mainPanel.add(pitchSlider, BorderLayout.EAST);
        mainPanel.add(renderPanel, BorderLayout.CENTER);

        frame.setTitle("Task4");
        frame.setSize(400, 400);
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel getPanelWithButton(){
        JPanel panel = new JPanel(new GridLayout(1, 5, 5, 1));
        JButton button1 = new JButton("Tetrahedron");
        button1.addActionListener(e -> renderPanel.tetrahedronButton());
        JButton button2 = new JButton("Cube");
        button2.addActionListener(e -> renderPanel.cubeButton());
        JButton button3 = new JButton("Octahedron");
        button3.addActionListener(e -> renderPanel.octahedronButton());
        JButton button4 = new JButton("Icosahedron");
        button4.addActionListener(e -> renderPanel.icosahedronButton());
        JButton button5 = new JButton("Dodecahedron");
        button5.addActionListener(e -> renderPanel.dodecahedronButton());

        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);
        panel.add(button5);

        return panel;
    }

    private static void initSlider(){
        headingSlider = new JSlider(0, 360, 180);
        headingSlider.addChangeListener(e -> renderPanel.repaint());

        pitchSlider = new JSlider(SwingConstants.VERTICAL, 0, 360, 180);
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
    }


    public static JSlider headingSlider = null;
    public static JSlider pitchSlider = null;

    public static RenderPanel renderPanel = null;


}
