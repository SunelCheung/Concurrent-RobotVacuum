import java.io.*;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 读取房间大小
            int roomSize = readRoomSize("room.txt");
            // 创建房间
            Room room = new Room(roomSize);
            // 读取机器人的详细信息
            List<Robot> robots = readRobots("robots.txt", room);
            // 创建模拟
            Simulation simulation = new Simulation(room, robots);

            // 启动模拟
            simulation.start(true);
        }
        catch (Exception e){
            switch (e.getClass().getSimpleName()) {
                case "IOException", "IllegalArgumentException" -> {
                    System.out.println("INPUT ERROR");
                    System.exit(0);
                }
                default -> {
                }
            }
        }
    }

    private static int readRoomSize(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
            return Integer.parseInt(reader.readLine());
    }

    private static List<Robot> readRobots(String filename, Room room) throws Exception {
        List<Robot> robots = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int numRobots = Integer.parseInt(reader.readLine());

        for (int i = 0; i < numRobots; i++) {
            String[] parts = reader.readLine().split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            char direction = parts[2].charAt(0);

            robots.add(new Robot(x, y, direction, room));
        }

        // assume the initial direction of travel of the centre robot vacuum is upwards.
        int centerIndex = room.size/2;
        if(room.cells[centerIndex][centerIndex].robot == null)
            robots.add(new Robot(centerIndex, centerIndex, 'U', room));

        return robots;
    }
}
