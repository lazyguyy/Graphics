package graphgui;

import graph.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class GraphFrame extends JFrame {
    public GraphFrame(Graph g) {
        super("Graphics");
        setBackground(Color.white);
        PainterPanel p = new PainterPanel(g, getBackground());
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
}
