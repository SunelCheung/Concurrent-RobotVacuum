import java.util.HashSet;

class Room {
    Cell[][] cells;
    int minX, minY, maxX, maxY, size;
    HashSet<Cell> dirtySet = new HashSet<>();

    Room(int size) {
        this.size = size;
        cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell(this);
                dirtySet.add(cells[i][j]);
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
                sb.append(cells[x - minX][y - minY]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    boolean checkClean(){
        if (dirtySet.isEmpty()){
            System.out.println("ROOM CLEAN");
            System.exit(0);
            return true;
        }
        return false;
    }
}
