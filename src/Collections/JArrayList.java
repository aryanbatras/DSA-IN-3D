package Collections;

import Shapes.Box;
import Shapes.Shape;
import Utility.Point;
import Utility.Color;
import Utility.Material;
import Animator.JArrayListAnimator;
import Utility.Video;

import java.io.IOException;
import java.util.ArrayList;

public class JArrayList {

    private ArrayList<Integer> arr;
    private JArrayListAnimator animator;

    public JArrayList() throws IOException {
        arr = new ArrayList<Integer>();
        animator = new JArrayListAnimator();
    }
    public JArrayList(int size){
        arr = new ArrayList<Integer>(size);
    }
    public void add(int value){
        arr.add(value);
        Box box = animator.addBox(value);
        animator.addAnimator(box, value);
    }
    public void remove(int index){
        arr.remove(index);
        Box box = animator.getBox(index);
        animator.removeAnimator(box, index);
    }
    public Integer get(int index){
        return arr.get(index);
    }


    public void generateVideo(){
        String framesDir = "/Users/aryanbatra/Desktop/DSA IN 3D/src/Resources/frames/";
        String outputVideoFolder = "/Users/aryanbatra/Desktop/DSA IN 3D/vid/";
        try {
            Video.generateVideo(framesDir, outputVideoFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
