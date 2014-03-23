package annotationtool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

class ControllerBox extends JFrame {

    private AnnotationTool annotationTool;
    private static final int SWATCH_SIZE = 24;

    private static class SwatchIcon implements Icon {

        private Paint paint;

        public SwatchIcon(Paint p) {
            System.out.println("Creating swatch");
            this.paint = p;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(paint);
            g2d.fillRect(x, y, SWATCH_SIZE, SWATCH_SIZE);
            if (((AbstractButton)c).isSelected()) {
                System.out.println("SELECTED...");
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g2d.drawRect(x, y, SWATCH_SIZE, SWATCH_SIZE);
            }
        }

        @Override
        public int getIconWidth() {
            return SWATCH_SIZE;
        }

        @Override
        public int getIconHeight() {
            return SWATCH_SIZE;
        }
    }

    private static class PaintPalletteItem {

        String name;
        Icon icon;
        Paint paint;

        public PaintPalletteItem(String name, Paint paint) {
            this.name = name;
            this.paint = paint;
            this.icon = new SwatchIcon(paint);
        }
    }

    private static final PaintPalletteItem[] paintPalletteItems = {
        new PaintPalletteItem("Red", new Color(255, 0, 0, 255)),
        new PaintPalletteItem("Orange", new Color(255, 128, 0, 255)),
        new PaintPalletteItem("Yellow", new Color(255, 255, 0, 255)),
        new PaintPalletteItem("Green", new Color(0, 255, 0, 255)),
        new PaintPalletteItem("Blue", new Color(0, 0, 255, 255)),
        new PaintPalletteItem("Violet", new Color(255, 0, 255, 255)),
        new PaintPalletteItem("Black", new Color(0, 0, 0, 255)),
        new PaintPalletteItem("White", new Color(255, 255, 255, 255)),
        new PaintPalletteItem("Highlighter Red", new Color(255, 0, 0, 128)),
        new PaintPalletteItem("Highlighter Orange", new Color(255, 128, 0, 128)),
        new PaintPalletteItem("Highlighter Yellow", new Color(255, 255, 0, 128)),
        new PaintPalletteItem("Highlighter Green", new Color(0, 255, 0, 128)),
        new PaintPalletteItem("Highlighter Blue", new Color(0, 0, 255, 128)),
        new PaintPalletteItem("Eraser", new Color(0, 0, 0, 0))
    };

    private static class PaintPalletteActionListener implements ActionListener {

        private AnnotationTool annotationTool;
        private Paint paint;

        public PaintPalletteActionListener(AnnotationTool at, PaintPalletteItem ppi) {
            annotationTool = at;
            paint = ppi.paint;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            annotationTool.setPaint(paint);
        }
    }

    private JRadioButton thinLine;
    private JRadioButton mediumLine;
    private JRadioButton thickLine;
    private JRadioButton hugeLine;

    public ControllerBox(AnnotationTool at) {
        super("Tools");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        annotationTool = at;
        setLayout(new GridLayout(30, 1));
        this.setAlwaysOnTop(true);

        ButtonGroup toolGroup = new ButtonGroup();
        boolean first = true;
        for (PaintPalletteItem ppi : paintPalletteItems) {
            JRadioButton jrb = new JRadioButton(ppi.name, ppi.icon, first);
//            jrb.setBackground((Color)ppi.paint);
            jrb.addActionListener(new PaintPalletteActionListener(at, ppi));
            add(jrb);
            toolGroup.add(jrb);
            if (first) {
                jrb.doClick();
                first = false;
            }
        }

        add(new JLabel("----------"));

        thinLine = new JRadioButton("Thin");
        thinLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.setStroke(
                        new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
        });
        add(thinLine);

        thinLine.doClick();

        mediumLine = new JRadioButton("Medium");
        mediumLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.setStroke(
                        new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
        });
        add(mediumLine);

        thickLine = new JRadioButton("Thick");
        thickLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.setStroke(
                        new BasicStroke(30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
        });
        add(thickLine);

        hugeLine = new JRadioButton("Huge");
        hugeLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.setStroke(
                        new BasicStroke(70, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
        });
        add(hugeLine);

        ButtonGroup thicknessGroup = new ButtonGroup();
        thicknessGroup.add(thinLine);
        thicknessGroup.add(mediumLine);
        thicknessGroup.add(thickLine);
        thicknessGroup.add(hugeLine);

        add(new JLabel("----------"));

        JButton eraseButton = new JButton("Erase Transparent");
        eraseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.doClear();
            }
        });
        add(eraseButton);

        JButton eraseWhiteButton = new JButton("Erase White");
        eraseWhiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.doClear(new Color(255, 255, 255, 255));
            }
        });
        add(eraseWhiteButton);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.undo();
            }
        });
        add(undoButton);

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.redo();
            }
        });
        add(redoButton);

        JButton killHistoryButton = new JButton("Clear History");
        killHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.clearHistory();
            }
        });
        add(killHistoryButton);

        add(new JLabel("----------"));

        JButton bringToTop = new JButton("Bring to top");
        bringToTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.toFront();
                annotationTool.setAlwaysOnTop(true);
            }
        });
        add(bringToTop);

        JButton sendBack = new JButton("Send to back");
        sendBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.setAlwaysOnTop(false);
                annotationTool.toBack();
            }
        });
        add(sendBack);

        JButton save = new JButton("Save image");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                annotationTool.doSave();
            }
        });
        add(save);

        JButton quit = new JButton("Exit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(
                        ControllerBox.this, "Confirm quit?", "Confirm quit",
                        JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        add(quit);
    }

}
