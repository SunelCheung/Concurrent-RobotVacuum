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

//        try {
//            Thread.sleep(SLEEP_TIME/10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        while (true) {
            try {
                Thread.sleep(SLEEP_TIME/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(showRoomStatus)
                System.out.println(room);
            if (room.checkClean()) {
                System.out.println("ROOM CLEAN");
                System.exit(0);
            }
            try {
                Thread.sleep(SLEEP_TIME/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
