/**
 * An animated histogram tool designed to be used with the Yahtzee assignment.
 * Adapted from Java Buddy's code, this can also be used to perform probability experiments.
 *
 * @author Stephen Adams
 * @version 202435000
 * Modified by Mychaylo Tatarynov to be multi-threaded.
 * web source <a href="http://java-buddy.blogspot.com/2015/07/apply-animaton-in-javafx-charts-with.html">BlogSpot Source of Chart</a>
 */

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * An animated histogram tool designed to be used with the Yahtzee assignment.
 * Adapted from Java Buddy's code, this can also be used to perform probability experiments.
 *
 * @author Stephen Adams
 * @version 202535000
 * Modified by Mychaylo Tatarynov to be multi-threaded.
 * web source <a href="http://java-buddy.blogspot.com/2015/07/apply-animaton-in-javafx-charts-with.html">BlogSpot Source of Chart</a>
 */
public class MonteCarloDemonstration extends Application {
    public static final int logicalCoreCount = Runtime.getRuntime().availableProcessors();
    public static final int iterations = 1_000_000;
    public static int iterationsRan = 0;
    public static boolean processing = true;

    public static double[] group = new double[1576]; // 0–1576 for possible Yahtzee scores
    public static int over150 = 0, over200 = 0;
    public static int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
    public static int scores = 0;

    @Override
    public void start(Stage primaryStage) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setCategoryGap(0);
        barChart.setBarGap(0);
        barChart.setAnimated(true);
        barChart.setMinHeight(Screen.getPrimary().getVisualBounds().getMaxY() * .65);

        xAxis.setLabel("Score");
        yAxis.setLabel("# of Games");

        // Improve readability of x-axis with many categories
        xAxis.setTickLabelRotation(90);
        xAxis.setTickLabelGap(2);
        xAxis.setTickMarkVisible(false);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Yahtzee Score Distribution");

        // Initialize bars, all scores start at 0
        for (int i = 0; i < group.length; i++) {
            series1.getData().add(new XYChart.Data<>(Integer.toString(i), group[i]));
        }

        barChart.getData().addAll(series1);

        Label labelCnt = new Label();
        labelCnt.setText("Iterations: " + iterationsRan);

        Label labelAnimated = new Label();
        labelAnimated.setText("Min Score:\t\t" + min + "\nMax Score:\t\t" + max +
                "\nGames>=150:\t\t" + over150 + "\nGames>=200:\t\t" + over200 +
                "\nAverage Score:\t\t0.00");

        VBox vBox = new VBox();
        vBox.setLayoutX(Screen.getPrimary().getVisualBounds().getMaxX() * 0.50 - 50);
        vBox.setLayoutY(Screen.getPrimary().getVisualBounds().getMaxY() * .8 + 25);
        vBox.getChildren().addAll(barChart, labelCnt, labelAnimated);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root,
                Screen.getPrimary().getBounds().getMaxX() * .8,
                Screen.getPrimary().getBounds().getMaxY() * .8);

        primaryStage.setTitle("Yahtzee Score Histogram");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Apply Animating Data in Charts
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(50), (ActionEvent actionEvent) -> {
                    if (iterationsRan == 0) {
                        return;
                    }

                    for (int i = 0; i < group.length; i++) {
                        ((XYChart.Data<String, Number>) series1.getData().get(i)).setYValue(group[i]);
                    }

                    labelCnt.setText("Iterations: " + iterationsRan + (iterationsRan == iterations ? " (finished)" : ""));

                    String text = String.format("""
                                    Min Score:\t\t%d
                                    Max Score:\t\t%d
                                    Games>=150:\t\t%d (%.2f%%)
                                    Games>=200:\t\t%d (%.2f%%)
                                    Average Score:\t\t%.2f""",
                            min, max,
                            over150, (double) over150 / iterationsRan * 100,
                            over200, (double) over200 / iterationsRan * 100,
                            (double) scores / iterationsRan);
                    labelAnimated.setText(text);

                    if (!processing) {
                        timeline.stop();
                    }
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        barChart.setAnimated(false);

        startProcessing();
    }

    /**
     * Starts the multi-threaded processing for the simulation.
     */
    public void startProcessing() {
        new Thread(() -> {
            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < logicalCoreCount; i++) {
                int workload = getWorkloadForThread(i, logicalCoreCount);

                Thread thread = new Thread(() -> {
                    for (int j = 0; j < workload; j++) {
                        addScore(new Yahtzee().play());
                    }
                });

                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            processing = false;
        }).start();
    }

    /**
     * Splits iterations among threads, assigning remainders to the last.
     */
    public int getWorkloadForThread(int threadNum, int totalThreads) {
        int workload = iterations / totalThreads;
        int remainder = iterations % totalThreads;
        return threadNum == totalThreads - 1 ? workload + remainder : workload;
    }

    /***
     * Adding scores is delegated to a synchronized method to avoid race conditions.
     */
    public static synchronized void addScore(int score) {
        iterationsRan++;
        scores += score;
        max = Math.max(score, max);
        min = Math.min(score, min);
        if (score >= 200) over200++;
        else if (score >= 150) over150++;

        group[score]++;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
