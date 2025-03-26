import java.awt.*;

public class FirstApp {
    public static void main(String[] args) {
        //You must create Frame.
        //If you create Window app or desktop app you must use Frame.
        //create obj of frame

      /*You have to set a layout by default it set layout Border layout. you have to
        change it according to your need. here change into flow layout*/

        Frame f = new Frame("My First App"); //Frame class ka obj keya-hai.
        f.setLayout(new FlowLayout());

        Button b = new Button("OK"); //This is a constructor.
        Label l = new Label("Name: ");
        TextField tf = new TextField(20); //Constructer you have to check JAVA Docs Oracle.

        f.add(l);
        f.add(tf);
        f.add(b);

        //you have to set it to visible to appear on the screen.
        //Important methods you have to remember it.
        f.setSize(300,300);
        f.setVisible(true);
    }
}

//Created a Frame and set the flow layout. so components looks like free size not occupied window whole.

//Creating all components taht we want to adding frame.

//Adding all of them yo a frame.

//setting frame size and displaying all of them.

//Basic approach to develop apps.