package name.haochenxie.jutilities.app;

import name.haochenxie.jutilities.labelprinter.ui.LabelEditor;
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

        JButton btnLabelPrinter = new JButton("Label Printer Utility");
        btnLabelPrinter.addActionListener($ ->
                new LabelEditor().display());
        contentPane.add(btnLabelPrinter);

        pack();
    }

    public static void main(String[] args) {
        Gui frame = new Gui();

        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
