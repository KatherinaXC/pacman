
public class PacManLauncher {

    /**
     * Initialize the PacManWorld. Takes a file representing a maze and two
     * arrays of strings representing names of PacManInterface based classes and
     * GhostInterface based classes respectively.
     */
    public static void main(String[] args) {
        //Main board is "src/PacMan/pacman.txt"
        //Alternate board is "src/PacMan/pacman_altboard.txt"
        PacManWorld pmw = new PacManWorld("src/PacMan/pacman.txt",
                new String[]{"PacMan"},
                new String[]{"Blinky", "Pinky", "Inky", "Clyde"});

        pmw.show();
    }
}
