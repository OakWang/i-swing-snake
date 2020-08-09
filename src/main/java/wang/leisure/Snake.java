package wang.leisure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.leisure.iswing.component.ILabel;
import wang.leisure.iswing.dom.Document;
import wang.leisure.iswing.dom.Node;
import wang.leisure.iswing.listener.IKeyListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Objects;

import static wang.leisure.App.isRun;
import static wang.leisure.App.allowAI;

/**
 * @author 东方雨倾
 * @since 1.0.0
 */
public class Snake extends IKeyListener {
    private static Logger log = LoggerFactory.getLogger(Snake.class);
    private Direction direction = Direction.EAST;
    private LinkedList<ILabel> snake = new LinkedList<>();


    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 38:
                turnDirection(Direction.NORTH);
                break;
            case 40:
                turnDirection(Direction.SOUTH);
                break;
            case 37:
                turnDirection(Direction.WEST);
                break;
            case 39:
                turnDirection(Direction.EAST);
                break;
            default:
                System.out.println("小蛇迷失了方向……");
        }
    }

    private enum Direction {
        NORTH, SOUTH, WEST, EAST;
    }

    public Snake(ILabel iLabel) {
        this.snake.addFirst(iLabel);
        Container container = Document.getNodeById("panel").getValue();
        container.setFocusable(true);
        container.addKeyListener(this);
        new Thread(this::run).start();
        log.info("初始化完成了");
    }

    private void run() {
        while (isRun) {
            ILabel iLabel = this.snake.peek();
            assert iLabel != null;
            switch (direction) {
                case NORTH:
                    goNorth(iLabel);
                    break;
                case SOUTH:
                    goSouth(iLabel);
                    break;
                case WEST:
                    goWest(iLabel);
                    break;
                case EAST:
                    goEast(iLabel);
                    break;
            }
            if (allowAI)
                ai();
            try {
                long sleepTime = allowAI ? 25 : 100;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                isRun = false;
            }
        }
    }

    private void turnDirection(Direction direction) {
        if (this.direction == Direction.NORTH && direction == Direction.SOUTH) {
            System.out.println("小蛇不能直接掉头！");
            return;
        }
        if (this.direction == Direction.SOUTH && direction == Direction.NORTH) {
            System.out.println("小蛇不能直接掉头！");
            return;
        }
        if (this.direction == Direction.WEST && direction == Direction.EAST) {
            System.out.println("小蛇不能直接掉头！");
            return;
        }
        if (this.direction == Direction.EAST && direction == Direction.WEST) {
            System.out.println("小蛇不能直接掉头！");
            return;
        }
        this.direction = direction;
    }

    private void goNorth(ILabel iLabel) {
        go(iLabel.getX(), iLabel.getY() - 25);
    }

    private void goSouth(ILabel iLabel) {
        go(iLabel.getX(), iLabel.getY() + 25);
    }

    private void goWest(ILabel iLabel) {
        go(iLabel.getX() - 25, iLabel.getY());
    }

    private void goEast(ILabel iLabel) {
        go(iLabel.getX() + 25, iLabel.getY());
    }

    private void go(int x, int y) {
        Point newPoint = new Point(x, y);
        Node apple = Document.getNodeById("apple");
        if (Objects.nonNull(apple)) {
            ILabel appleBtn = (ILabel) apple.getValue();
            Point appLoc = appleBtn.getLocation();
            if (appLoc.x == newPoint.x && appLoc.y == newPoint.y) {
                this.snake.addFirst(appleBtn);
                Document.removeNodeById("apple");
                return;
            }
        }

        Point point = null;
        for (ILabel iLabel : this.snake) {
            if (point == null) {
                point = iLabel.getLocation();
                iLabel.setLocation(newPoint);
                iLabel.setBackground(new Color(Integer.parseInt("7FFFAA", 16)));
                continue;
            }
            Point temp = iLabel.getLocation();
            iLabel.setLocation(point);
            point = temp;
            iLabel.repaint();
        }
    }

    private boolean closeToWall() {
        Container head = this.snake.peek();
        assert head != null;
        return head.getX() == 0 || head.getY() == 0 || head.getX() == 775 || head.getY() == 775;
    }

    /**
     * 这段代码不要看，纯属于无聊时写写玩的。
     * 有BUG（滑稽）
     */
    private void ai() {
        Node node = Document.getNodeById("apple");
        if (node != null) {
            Container apple = node.getValue();
            Point point = apple.getLocation();
            Container head = this.snake.peek();
            assert head != null;
            if (closeToWall()) {
                if (head.getX() <= 0 && head.getY() > 0 && head.getY() < 775
                        && (direction == Direction.SOUTH || direction == Direction.NORTH)) {
                    direction = Direction.EAST;
                    return;
                }
                if (head.getX() <= 0 && head.getY() > 0 && head.getY() < 775
                        && direction == Direction.WEST) {
                    if (head.getY() < point.y)
                        direction = Direction.SOUTH;
                    else
                        direction = Direction.NORTH;
                    return;
                }
                if (head.getX() <= 0 && head.getY() >= 775 && direction == Direction.SOUTH) {
                    direction = Direction.EAST;
                    return;
                }
                if (head.getX() <= 0 && head.getY() >= 775 && direction == Direction.WEST) {
                    direction = Direction.NORTH;
                    return;
                }
                if (head.getX() <= 0 && head.getY() == 0 && direction == Direction.NORTH) {
                    direction = Direction.EAST;
                    return;
                }
                if (head.getX() <= 0 && head.getY() == 0 && direction == Direction.WEST) {
                    direction = Direction.SOUTH;
                    return;
                }

                if (head.getX() > 0 && head.getY() <= 0 && head.getX() < 775
                        && (direction == Direction.EAST || direction == Direction.WEST)) {
                    direction = Direction.SOUTH;
                    return;
                }
                if (head.getX() > 0 && head.getY() <= 0 && head.getX() < 775
                        && direction == Direction.NORTH) {
                    if (head.getX() < point.x)
                        direction = Direction.EAST;
                    else
                        direction = Direction.WEST;
                    return;
                }
                if (head.getX() >= 775 && head.getY() <= 0 && direction == Direction.EAST) {
                    direction = Direction.SOUTH;
                    return;
                }
                if (head.getX() >= 775 && head.getY() <= 0 && direction == Direction.NORTH) {
                    direction = Direction.WEST;
                    return;
                }
                if (head.getX() >= 775 && head.getY() >= 775 && direction == Direction.SOUTH) {
                    direction = Direction.WEST;
                    return;
                }
                if (head.getX() >= 775 && head.getY() >= 775 && direction == Direction.EAST) {
                    direction = Direction.NORTH;
                    return;
                }
            }
            if (head.getX() < point.x && direction == Direction.EAST) return;
            if (head.getX() < point.x && direction == Direction.WEST) {
                direction = Direction.NORTH;
                return;
            }
            if (head.getX() < point.x && direction == Direction.NORTH) {
                direction = Direction.EAST;
                return;
            }
            if (head.getX() < point.x && direction == Direction.SOUTH) {
                direction = Direction.EAST;
                return;
            }
            if (head.getX() > point.x && direction == Direction.WEST) return;
            if (head.getX() > point.x && direction == Direction.EAST) {
                direction = Direction.NORTH;
                return;
            }
            if (head.getX() > point.x && direction == Direction.NORTH) {
                direction = Direction.WEST;
                return;
            }
            if (head.getX() > point.x && direction == Direction.SOUTH) {
                direction = Direction.WEST;
                return;
            }
            if (head.getX() == point.x && head.getY() < point.y && (direction == Direction.EAST || direction == Direction.WEST)) {
                direction = Direction.SOUTH;
                return;
            }
            if (head.getX() == point.x && head.getY() > point.y && (direction == Direction.EAST || direction == Direction.WEST)) {
                direction = Direction.NORTH;
            }
        }
    }
}
