import java.io.*;
import java.util.*;
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
    int size;
    int minX, minY, maxX, maxY;

    Room(int size) {
        this.size = size;
        cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
        minX = minY = 0;
        maxX = maxY = size - 1;
    }
}

class Robot implements Runnable {
    static int sleepTime = 2;
    int x, y;
    char direction;
    Room room;
    int initX, initY;

    Robot(int x, int y, char direction, Room room) {
        this.x = initX = x;
        this.y = initY = y;
        this.direction = direction;
        this.room = room;
        room.cells[x][y].robot = this;
    }

    @Override
    public void run() {
        while (true) {
            var lock = room.cells[x][y].lock;
            lock.writeLock().lock();
            // Clean the current cell
            room.cells[x][y].isClean = true;
            // Remove the robot from the current cell
            room.cells[x][y].robot = null;
            lock.writeLock().unlock();

            // Move in the current direction
            switch (direction) {
                case 'U':
                    if (y < room.maxY) {
                        y++;
                    }
                    break;
                case 'L':
                    if (x > room.minX) {
                        x--;
                    }
                    break;
                case 'D':
                    if (y > room.minY) {
                        y--;
                    }
                    break;
                case 'R':
                    if (x < room.maxX) {
                        x++;
                    }
                    break;
            }

            lock = room.cells[x][y].lock;
            lock.readLock().lock();

            // Check for collisions
            if (room.cells[x][y].robot != null) {
                System.out.println("COLLISION AT CELL (" + x + ";" + y + ")");
                System.exit(0);
            }

            lock.readLock().unlock();
            lock.writeLock().lock();
            // Place the robot in the new cell
            room.cells[x][y].robot = this;
            lock.writeLock().unlock();

            // Change direction in a counter-clockwise spiral pattern
            if (direction == 'U' && (x - initX <= y - initY)) {
                direction = 'L';
            } else if (direction == 'L' && (x - initX == y - initY)) {
                direction = 'D';
            } else if (direction == 'D' && (x - initX >= y - initY)) {
                direction = 'R';
            } else if (direction == 'R' && (x - initX - 1 == y - initY)) {
                direction = 'U';
            }

            // Sleep for the specified delay
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Simulation {
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
    }
}

public class Main {
    public static void main(String[] args) {
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

    private static int readRoomSize(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException("Error reading room size", e);
        }
    }

    private static List<Robot> readRobots(String filename, Room room) {
        List<Robot> robots = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int numRobots = Integer.parseInt(reader.readLine());

            for (int i = 0; i < numRobots; i++) {
                String[] parts = reader.readLine().split(" ");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                char direction = parts[2].charAt(0);

                robots.add(new Robot(x, y, direction, room));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading robots", e);
        }

        return robots;
    }
}
