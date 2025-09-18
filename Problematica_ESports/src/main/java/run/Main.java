package run;
import models.*;
import utils.ConsoleIO;
import viewModel.AppVM;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ConsoleIO io = new ConsoleIO();
        AppVM app = new AppVM(io);
        app.runMenuLoop();
    }

}
