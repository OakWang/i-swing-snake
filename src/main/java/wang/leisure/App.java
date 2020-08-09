package wang.leisure;

import wang.leisure.iswing.component.IFrame;
import wang.leisure.iswing.component.ILabel;
import wang.leisure.iswing.component.IPanel;
import wang.leisure.iswing.dom.Document;
import wang.leisure.iswing.dom.Node;

import java.awt.Color;
import java.util.Random;

public class App {
    public static boolean isRun = true;
    public static boolean allowAI = false;
    static Random random = new Random();
    public static void main(String[] args) {
        IFrame.buildWorld("src/main/resources/snake.swing");
        new Snake((ILabel) Document.getNodeById("snake").getValue());
        new Thread(() -> {
            try {
                produceApple();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void produceApple() throws InterruptedException {
        while (isRun) {
            Thread.sleep(100L);
            Node node = Document.getNodeById("apple");
            if (node != null)
                continue;
            int x = random.nextInt(776)/25*25;
            int y = random.nextInt(776)/25*25;
            ILabel iLabel = new ILabel();
            iLabel.setBounds(x, y, 25,25);
            iLabel.setOpaque(true);
            iLabel.setBackground(Color.RED);
            iLabel.setVisible(true);
            Node apple = new Node("apple", iLabel);
            IPanel iPanel = (IPanel) Document.getNodeById("panel").getValue();
            iPanel.add(iLabel);
            iPanel.repaint();
            Document.add(apple);
        }
    }
}
