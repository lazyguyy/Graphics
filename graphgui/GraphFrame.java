package graphgui;

import graph.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class GraphFrame extends JFrame {
    PainterPanel p;
    public GraphFrame(Graph g) {
        super("Graphics");
        setBackground(Color.white);
        p = new PainterPanel(g, getBackground());
        p.init();
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
    }
    
    public void update(Graph g) {
        p.update(g);
    }
}
