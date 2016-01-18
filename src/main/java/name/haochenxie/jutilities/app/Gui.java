package name.haochenxie.jutilities.app;

import name.haochenxie.jutilities.feagencurve.FeagencurveTry;
import name.haochenxie.jutilities.spectrumchart.SpectrumChartPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by haochen on 1/4/16.
 */
public class Gui extends JFrame {

    public Gui() {
        init();
    }

    private void init() {
        setTitle("Haochen's Collection of Utilities");

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JButton btnSpectrumChart = new JButton("Spectrum Chart Creator");
        btnSpectrumChart.addActionListener($ ->
                new SpectrumChartPanel().display(SpectrumChartPanel.getSampleDataText()));
        contentPane.add(btnSpectrumChart);

        JButton btnFeagencurve = new JButton("Feagencurve Prototype");
        btnFeagencurve.addActionListener($ ->
                FeagencurveTry.main(new String[0]));
        contentPane.add(btnFeagencurve);

        pack();
    }

    public static void main(String[] args) {
        Gui frame = new Gui();

        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
