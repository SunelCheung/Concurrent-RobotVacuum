import java.awt.*;

class Robot implements Runnable {
    static Point[] stepVector = {
            new Point(0, 1),
            new Point(-1, 0),
            new Point(0, -1),
            new Point(1, 0)};
    int x, y;
    int direction;
    Room room;
    boolean isRunning = false;

    Robot(int x, int y, char direction, Room room) {
        this.x = x;
        this.y = y;
        switch (direction) {
            case 'U' -> this.direction = 0;
            case 'L' -> this.direction = 1;
            case 'D' -> this.direction = 2;
            case 'R' -> this.direction = 3;
        }
        this.room = room;
        Cell cell = room.cells[x][y];
        cell.lock.lock();
        if(cell.robot != null)
        {
            throw new IllegalArgumentException("Robot already exists in cell (" + x + "," + y + ")");
        }
        else {
            cell.robot = this;
        }
        cell.lock.unlock();
    }

    private boolean stepRun(int direction, int step) {
        direction %= 4;
        for (int k = 0; k < step; k++) {
            Cell cell = room.cells[x][y];
            x += stepVector[direction].x;
            y += stepVector[direction].y;

            // hit a wall
            if (x > room.maxX || y > room.maxY || x < room.minX || y < room.minY){
                x -= stepVector[direction].x;
                y -= stepVector[direction].y;
                return false;
            }

            cell.lock.lock();
            // Clean the current cell
            room.clean(cell);
            // Remove the robot from the current cell
            if(room.checkClean())
                return false;
            cell.robot = null;
            cell.lock.unlock();

            cell = room.cells[x][y];

            cell.lock.lock();
            // Check for collisions
            if (room.cells[x][y].robot != null) {
                System.out.println("COLLISION AT CELL (" + x + "," + y + ")");
                System.exit(0);
            }
            // Place the robot in the new cell
            room.cells[x][y].robot = this;
            cell.lock.unlock();

            // Sleep for 2 seconds to simulate the time it takes to clean a cell
            try {
                Thread.sleep(Simulation.SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void run() {
        int step = 0;

        // Sleep for 2 seconds to simulate the time it takes to clean a cell
        try {
            Thread.sleep(Simulation.SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            /*
          If a robot is at the boundary of the room and about to hit a wall,
          then it should turn counterclockwise and continue travelling counterclockwise
          along the walls of the room in a circular loop, and no longer a spiral pattern.
             */
            if (!stepRun(direction++, ++step)) {
                step = room.size;
            }
            if (!stepRun(direction++, step)) {
                step = room.size;
            }
            if (!stepRun(direction++, ++step)) {
                step = room.size;
            }
            if (!stepRun(direction++, step)) {
                step = room.size;
            }
        }
    }
}
