package com.example.computer_graphics_lab3;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.paint.Color;

import java.awt.Point;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextField x1Field;
    @FXML
    public TextField y1Field;
    @FXML
    public TextField x2Field;
    @FXML
    public TextField y2Field;
    @FXML
    public Canvas canvas;
    @FXML
    public TextArea resultTextArea;

    //canvas controls
    private List<Point> points;
    private GraphicsContext graphicsContext;
    private float scale;
    private int translateX;
    private int translateY;
    private int lastX;
    private int lastY;

    @FXML
    public void OnBresenhamButton(){
        int x1 = Integer.parseInt(x1Field.getText());
        int y1 = Integer.parseInt(y1Field.getText());
        int x2 = Integer.parseInt(x2Field.getText());
        int y2 = Integer.parseInt(y2Field.getText());

        long startTime = System.nanoTime();
        List<Point> points = bresenhamLineAlgorithm(x1, y1, x2, y2);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        SetPoints(points);
        repaint();
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(duration).append(" nanoseconds\n");
        for (Point point : points) {
            sb.append("(").append(point.x).append(", ").append(point.y).append(")\n");
        }
        resultTextArea.setText(sb.toString());
    }
    @FXML
    public void OnStepByStepButton(){
        int x1 = Integer.parseInt(x1Field.getText());
        int y1 = Integer.parseInt(y1Field.getText());
        int x2 = Integer.parseInt(x2Field.getText());
        int y2 = Integer.parseInt(y2Field.getText());

        long startTime = System.nanoTime();
        List<Point> points = stepByStepLineAlgorithm(x1, y1, x2, y2);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        SetPoints(points);
        repaint();
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(duration).append(" nanoseconds\n");
        for (Point point : points) {
            sb.append("(").append(point.x).append(", ").append(point.y).append(")\n");
        }
        resultTextArea.setText(sb.toString());
    }
    private void SetPoints(List<Point> points){
        this.points = points;
    }

    private List<Point> stepByStepLineAlgorithm(int x1, int y1, int x2, int y2) {
        List<Point> points = new ArrayList<>();
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            points.add(new Point(Math.round(x), Math.round(y)));
            x += xIncrement;
            y += yIncrement;
        }
        return points;
    }


    private List<Point> bresenhamLineAlgorithm(int x1, int y1, int x2, int y2) {
        List<Point> points = new ArrayList<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            points.add(new Point(x1, y1));
            int err2 = 2 * err;
            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        points.add(new Point(x2, y2));
        return points;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scale = 1.0f;
        translateX = 0;
        translateY = 0;
        graphicsContext = canvas.getGraphicsContext2D();
        repaint();
        canvas.setOnScroll(scrollEvent -> {
            double delta = scrollEvent.getDeltaY();
            if(delta < 0){
                scale *= 1.1;
            } else {
                scale *= 0.9;
            }
            repaint();
        });
        canvas.setOnMousePressed(mouseEvent -> {
            lastX = (int) mouseEvent.getX();
            lastY = (int) mouseEvent.getY();
        });
        canvas.setOnMouseDragged(mouseEvent -> {
            int dx = (int) (mouseEvent.getX() - lastX);
            int dy = (int) (mouseEvent.getY() - lastY);
            translateX += dx;
            translateY += dy;
            lastX = (int) mouseEvent.getX();
            lastY = (int) mouseEvent.getY();
            repaint();
        });
    }

    private int reX(int x){
        return (int) ((x + translateX) * scale);
    }
    private int reY(int y){
        return (int) ((y + translateY) * scale);
    }

    private void drawLine(int x1, int y1, int x2, int y2){
        graphicsContext.beginPath();
        graphicsContext.moveTo(x1, y1);
        graphicsContext.lineTo(x2, y2);
        graphicsContext.closePath();
        graphicsContext.stroke();
    }
    private void repaint(){
        int width = 500;
        int height = 500;
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, width, height);

        graphicsContext.setFill(Color.BLACK);

        drawLine(0 ,reY(height / 2), width, reY(height / 2));
        drawLine(reX(width / 2), 0, reX(width / 2), height);
        int gridSize = 20;
        int halfGridSize = gridSize / 2;
        for (int i = 0; i < (width - translateX)/scale; i += gridSize) {
            drawLine(reX(width / 2 + i), reY(height / 2 - halfGridSize),
                    reX(width / 2 + i), reY(height / 2 + halfGridSize));
        }
        for (int i = 0; i < (height -translateY) / scale; i += gridSize) {
            drawLine(reX(width / 2 - halfGridSize), reY(height / 2 + i),
                    reX(width / 2 + halfGridSize), reY(height / 2 + i));
        }
        for (int i = 0; i > (-width -translateX)/scale; i -= gridSize) {
            drawLine(reX(width / 2 + i), reY(height / 2 - halfGridSize),
                    reX(width / 2 + i), reY(height / 2 + halfGridSize));
        }
        for (int i = 0; i > -(height + translateY) / scale; i -= gridSize) {
            drawLine(reX(width / 2 - halfGridSize), reY(height / 2 + i),
                    reX(width / 2 + halfGridSize), reY(height / 2 + i));
        }
        if (points != null) {
            graphicsContext.setFill(Color.RED);
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                if (p2.y < p1.y) {
                    if (p2.x < p1.x) {
                        int x = width / 2 + p1.x * gridSize - gridSize;
                        int y = height / 2 - p1.y * gridSize;
                        graphicsContext.fillRect(reX(x), reY(y), (int)(gridSize * scale),(int)(scale* gridSize));
                    } else {
                        int x = width / 2 + p1.x * gridSize;
                        int y = height / 2 - p1.y * gridSize;
                        graphicsContext.fillRect(reX(x),reY( y), (int)(gridSize * scale),(int)(scale* gridSize));
                    }
                } else {
                    if (p2.x < p1.x) {
                        int x = width / 2 + p1.x * gridSize - gridSize;
                        int y = height / 2 - p1.y * gridSize - gridSize;
                        graphicsContext.fillRect(reX(x), reY(y), (int)(gridSize * scale),(int)(scale* gridSize));
                    } else {
                        int x = width / 2 + p1.x * gridSize;
                        int y = height / 2 - p1.y * gridSize - gridSize;
                        graphicsContext.fillRect(reX(x), reY(y), (int)(gridSize * scale),(int)(scale* gridSize));
                    }
                }
            }
        }
    }
}