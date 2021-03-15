package bspsrc;

public final class Main {

    public static void main(String[] args) throws Exception {
        if(System.console() == null) {
            MainGUI.main(args);
        }else{
            MainCLI.main(args);
        }
    }
}