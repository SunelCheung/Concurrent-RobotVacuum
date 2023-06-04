import java.util.List;

class Simulation {
    public static final int SLEEP_TIME = 200;

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

        while (showRoomStatus) {
            try {
                Thread.sleep(SLEEP_TIME/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(room);
            try {
                Thread.sleep(SLEEP_TIME/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
