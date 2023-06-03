import java.io.*;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int roomSize;
        Room room = null;
        List<Robot> robots = null;
        try {
            roomSize = readRoomSize("room.txt");
            room = new Room(roomSize);
            robots = readRobots("robots.txt", room);
        }
        catch (Exception e){
            System.out.println("INPUT ERROR");
            System.exit(-1);
        }

        try {
            new Simulation(room, robots).start(true);
        }
        catch (Exception e){
            System.out.println(e);
            System.exit(-1);
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
