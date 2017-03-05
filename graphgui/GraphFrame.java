package graphgui;

import graph.*;
import graphgui.dataManagement.Settings;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class GraphFrame extends JFrame {
    PainterPanel p;
    public GraphFrame(Graph g) {
        super("Graphics");
        setBackground(Color.white);
        p = new PainterPanel(g, getBackground());
        add(p);
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w) {
                p.stop();
                dispose();
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
        p.init();
    }
    
    public void updateGraph(Graph g) {
        p.updateGraph(g);
    }
    
    public void updateProperties(Settings p) {
        this.p.updateProperties(p);
    }
}
