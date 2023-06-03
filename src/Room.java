class Room {
    Cell[][] cells;
    int minX, minY, maxX, maxY, size;

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

    boolean checkClean(){
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Cell cell = cells[x][y];
                if (!cell.isClean) {
                    return false;
                }
            }
        }

        return true;
    }
}
