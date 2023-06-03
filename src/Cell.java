import java.util.concurrent.locks.ReentrantReadWriteLock;

class Cell {
    boolean isClean;
    Robot robot;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Cell() {
        isClean = false;
        robot = null;
    }
}