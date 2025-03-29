package CheckBoxDemo;

// Checkbox RadioButton And ItemListener

import java.awt.*;
import java.awt.event.*;

class myFrame extends Frame implements ItemListener
{
    Label l;
    Checkbox c1,c2,c3;
    //Set of the checkboxes are kept inside the group then it become a RadioButton.
    CheckboxGroup cbg;

    public myFrame()
    {
        super("Check Box Demo");
        l=new Label("Nothing is selected");
        c1=new Checkbox("JAVA",false,cbg);
        c2=new Checkbox("Python",false,cbg);
        c3=new Checkbox("C++",false,cbg);

        c1.addItemListener(this);
        c2.addItemListener(this);
        c3.addItemListener(this);

        setLayout(new FlowLayout());

        add(l);
        add(c1);
        add(c2);
        add(c3);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        String str = "";
        if(c1.getState())
            str=str+" "+c1.getLabel();
        if(c2.getState())
            str=str+" "+c2.getLabel();
        if(c3.getState())
            str=str+" "+c3.getLabel();
        //Before setting text check Nothing is selected
        if(str.isEmpty())
            str="Nothing is selected.";
        l.setText(str);


    }
}

public class CheckBox {
    public static void main(String[] args) {
        myFrame m = new myFrame();
        m.setSize(400,400);
        m.setVisible(true);
    }
}