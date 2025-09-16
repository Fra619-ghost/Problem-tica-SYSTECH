package run;

import utils.ConsoleIO;
import viewModels.AppVM;

public class Main {
    public static void main(String[] args) {
        ConsoleIO io = new ConsoleIO();
        AppVM app = new AppVM(io);
        app.runMenuLoop();
    }
}