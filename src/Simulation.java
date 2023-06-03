import java.util.List;

class Simulation {
    public static final int SLEEP_TIME = 2000;

    List<Robot> robots;
    Room room;

    Simulation(Room room, List<Robot> robots) {
        this.room = room;
        this.robots = robots;
    }

    void start(boolean showRoomStatus) {
        for (Robot robot : robots) {
            new Thread(robot).start();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            boolean allClean = true;
            for (int x = room.minX; x <= room.maxX; x++) {
                for (int y = room.minY; y <= room.maxY; y++) {
                    Cell cell = room.cells[x][y];
                    if (!cell.isClean) {
                        allClean = false;
                        break;
                    }
                }
                if (!allClean) {
                    break;
                }
            }
            if (allClean) {
                System.out.println("ROOM CLEAN");
                System.exit(0);
            }
            try {
                if(showRoomStatus)
                    System.out.println(room);
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
