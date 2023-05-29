import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.*;

class Cell {
    boolean isClean;
    Robot robot;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Cell() {
        isClean = false;
        robot = null;
    }
}

class Room {
    Cell[][] cells;
    int minX, minY, maxX, maxY;

    Room(int size) {
        cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
        minX = minY = 0;
        maxX = maxY = size - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                Cell cell = cells[x - minX][y - minY];
                if (cell.robot != null) {
                    if(cell.robot.isRunning)
                        sb.append("R");
                    else
                        sb.append("■");
                } else if (cell.isClean) {
                    sb.append(".");
                } else {
                    sb.append("#");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

class Robot implements Runnable {
    static Point[] stepVector = {new Point(0, 1), new Point(-1, 0), new Point(0, -1), new Point(1, 0)};
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
        cell.lock.writeLock().lock();
        if(cell.robot != null)
        {
            throw new IllegalArgumentException("Robot already exists in cell (" + x + "," + y + ")");
        }
        else {
            cell.robot = this;
        }
        cell.lock.writeLock().unlock();
    }

    private boolean stepRun(int direction, int step) {
        direction %= 4;
        for (int k = 0; k < step; k++) {
            // Sleep for 2 seconds to simulate the time it takes to clean a cell
            try {
                Thread.sleep(Simulation.SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            Cell cell = room.cells[x][y];
//            System.out.println(Thread.currentThread().threadId()+ "step vector (" + stepVector[direction].x + ";" + stepVector[direction].y + ")");
            x += stepVector[direction].x;
            y += stepVector[direction].y;
            if (x > room.maxX || y > room.maxY || x < room.minX || y < room.minY)
                return false;
            else
            {
                cell.lock.writeLock().lock();
                // Clean the current cell
                cell.isClean = true;
                // Remove the robot from the current cell
                cell.robot = null;
                cell.lock.writeLock().unlock();
            }
            
            cell = room.cells[x][y];
            cell.lock.readLock().lock();

            // Check for collisions
            if (room.cells[x][y].robot != null) {
                System.out.println("COLLISION AT CELL (" + x + "," + y + ")");
                System.exit(0);
            }

            cell.lock.readLock().unlock();
            cell.lock.writeLock().lock();
            // Place the robot in the new cell
            room.cells[x][y].robot = this;
            cell.lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public void run() {
        int step = 0;
        isRunning = true;
        while (true) {
            if (!stepRun(direction++, ++step)) {
                break;
            }
            if (!stepRun(direction++, step)) {
                break;
            }

            if (!stepRun(direction++, ++step)) {
                break;
            }
            if (!stepRun(direction++, step)) {
                break;
            }
        }
        isRunning = false;
    }
}

class Simulation {
    public static final int SLEEP_TIME = 1000;

    List<Robot> robots;
    Room room;

    Simulation(Room room, List<Robot> robots) {
        this.room = room;
        this.robots = robots;
    }

    void start() {
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
                System.out.println(room);
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

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
            simulation.start();
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
        return robots;
    }
}
