
public class PacManLauncher {

    public static void main(String[] args) {
        /**
         * Initialize the PacManWorld takes a file representing a maze and two
         * arrays of strings representing names of PacManInterface based classes
         * and GhostInterface based classes respectively.
         */
        PacManWorld pmw = new PacManWorld("src/PacMan/pacman.txt",
                new String[]{"PacMan"},
                new String[]{"Blinky", "Clyde", "Inky", "Pinky"});

        pmw.show();
    }
}
