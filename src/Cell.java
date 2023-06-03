import java.util.concurrent.locks.ReentrantLock;

class Cell {
    boolean isClean;
    Robot robot;
    ReentrantLock lock = new ReentrantLock();
    Cell() {
        isClean = false;
        robot = null;
    }
}