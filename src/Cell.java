import java.util.concurrent.locks.ReentrantLock;

class Cell {
    boolean isClean(){
        return !room.dirtySet.contains(this);
    }

    Robot robot;
    Room room;
    ReentrantLock lock = new ReentrantLock();
    Cell(Room room) {
        this.room = room;
        robot = null;
    }

    public String toString() {
        if (robot != null) {
            if(robot.isRunning)
                return ("R");
            else
                return ("■");
        } else if (isClean()) {
            return (".");
        } else {
            return ("#");
        }
    }
}