package Animations.Animator;

import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Shapes.JBox;
import Rendering.View;

import Utility.*;
import Utility.Renderer;
import Shapes.Core.Shape;

import Rendering.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

public class JMinHeapAnimator<T extends Comparable<T>> {
    private final ArrayList<Shape> world;
    private final Utility.Camera camera;
    private final Renderer renderer;
    private final Subtitle subtitle;

    private int framesPerSecond;
    private final BoxAnimator boxAnimator;
    private final CameraAnimator cameraAnimator;

    private Render mode;
    private double scale;
    private Effect particle;
    private Texture material;
    private String background;
    private final Random rand;
    private boolean randomBackground;

    private final ArrayList<T> heapList = new ArrayList<>( );
    private final Map<T, JBox> valueToBox = new HashMap<>( );
    private Map<TreeNode, JBox> nodeToBox = new HashMap<>( );


    public JMinHeapAnimator() {
        this.scale = 0.5;
        this.framesPerSecond = 20;
        this.world = new ArrayList<>( );
        this.material = Texture.METAL;
        this.particle = Effect.NONE;
        this.camera = new Utility.Camera( );
        this.background = "Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("BinaryTree");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.randomBackground = false;
        this.mode = Render.DISABLED;
        this.rand = new Random( );
    }

    public JBox setGoldPointer() {
        return new JBox(
                new Point(0, 0, 0.1),
                0, 0.42, 0.1,
                new Color(1.0f, 0.82f, 0.0f),
                Texture.METAL, 0.0f, null,
                particle
        );
    }

    public void setFPS(int fps) {
        this.framesPerSecond = fps;
        cameraAnimator.setFPS(fps);
        boxAnimator.setFPS(fps);
    }

    public void setMode(Render mode) {
        this.mode = mode;
        cameraAnimator.setMode(mode);
        boxAnimator.setMode(mode);
    }

    public void setEncoder(Encoder encoder) {
        renderer.setEncoder(encoder);
    }

    public void setScale(double scale) {
        this.scale = scale;
        renderer.setScale(background, scale);
    }

    public void setBackground(String background) {
        this.background = background;
        renderer.setBackground(background);
    }

    public void setMaterial(Texture material) {
        this.material = material;
    }

    public void setParticle(Effect particle) {
        this.particle = particle;
    }

    public void setAntiAliasing(double antiAliasing) {
        renderer.setAntialiasing(antiAliasing);
    }

    public void setCameraRotation(View rotationType) {
        cameraAnimator.setCameraRotation(rotationType);
        boxAnimator.setCameraRotation(cameraAnimator, rotationType);
    }

    public void setCameraSpeed(double speed) {
        cameraAnimator.setSpeed(speed);
        boxAnimator.setSpeed(speed);
    }

    private void setRandomBackground() {
        if(mode == Render.DISABLED) return;
        Scenery randomBg = Scenery.values( )[rand.nextInt(Scenery.values( ).length)];
        setBackground(randomBg.toString( ));
    }

    public void setRandomizeBackgroundAsTrue() {
        this.randomBackground = true;
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius( ) - focus);
    }


    private JMinHeapAnimator.TreeNode root;

    private class TreeNode {
        T value;
        JMinHeapAnimator.TreeNode left;
        JMinHeapAnimator.TreeNode right;

        TreeNode(T value) {
            this.value = value;
        }
    }

    private TreeNode findParent(TreeNode root, TreeNode child) {
        if (root == null || root == child) return null;
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>( );
        q.add(root);
        while (!q.isEmpty( )) {
            TreeNode n = q.poll( );
            if (n.left == child || n.right == child) {
                return n;
            }
            if (n.left != null) q.add(n.left);
            if (n.right != null) q.add(n.right);
        }
        return null;
    }

    private TreeNode findNode(TreeNode root, T target) {
        if (root == null) return null;
        TreeNode found = null;
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>( );
        q.add(root);
        while (!q.isEmpty( )) {
            TreeNode n = q.poll( );
            if (n.value.equals(target)) {
                found = n;
            }
            if (n.left != null) q.add(n.left);
            if (n.right != null) q.add(n.right);
        }
        return found;
    }

    private void siftUp(TreeNode node) {
        while (true) {

            TreeNode parent = findParent(root, node);
            if (parent == null) break;

            boolean shouldSwap = ((Comparable<T>) node.value).compareTo(parent.value) < 0;
            if (!shouldSwap) break;

            JBox childBox = nodeToBox.get(node);
            JBox parentBox = nodeToBox.get(parent);
            if (childBox == null || parentBox == null) break;
            Point cPos = childBox.getCenter( );
            Point pPos = parentBox.getCenter( );

            cameraAnimator.slideTo(childBox.getCenter( ));
            boxAnimator.highlight(childBox);

            cameraAnimator.slideTo(parentBox.getCenter( ));
            boxAnimator.highlight(parentBox);

            T tmp = node.value;
            node.value = parent.value;
            parent.value = tmp;

            childBox.val = node.value.toString( );
            parentBox.val = parent.value.toString( );
            valueToBox.put(node.value, childBox);
            valueToBox.put(parent.value, parentBox);

            boxAnimator.scalePop(parentBox);

            node = parent;
        }
    }

    private void collectLevelOrder(TreeNode root, ArrayList<TreeNode> out) {
        if (root == null) return;
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>( );
        q.add(root);
        while (!q.isEmpty( )) {
            TreeNode n = q.poll( );
            out.add(n);
            if (n.left != null) q.add(n.left);
            if (n.right != null) q.add(n.right);
        }
    }

    private int heapSize = 0;

    private TreeNode insertIntoHeap(TreeNode root, T value) {
        ArrayList<TreeNode> nodeList = new ArrayList<>( );
        collectLevelOrder(root, nodeList);

        TreeNode newNode = new TreeNode(value);
        if (nodeList.isEmpty( )) {
            heapSize = 1;
            return newNode;
        }
        TreeNode parent = nodeList.get((nodeList.size( ) - 1) / 2);
        if (parent.left == null) parent.left = newNode;
        else parent.right = newNode;
        heapSize = nodeList.size( ) + 1;
        return root;
    }

    public void runAddAnimation(T value, Entrance animation) {
        if (randomBackground) setRandomBackground( );
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep( );
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Inserting");
        subtitle.setValue(String.valueOf(value));

        root = insertIntoHeap(root, value);

        TreeNode newNode = findNode(root, value);

        ArrayList<TreeNode> nodes = new ArrayList<>( );
        collectLevelOrder(root, nodes);

        Map<Integer, Integer> countAtDepth = new HashMap<>( );
        Map<T, Integer> indexAtDepth = new HashMap<>( );
        Map<T, Integer> depthMap = new HashMap<>( );

        double baseVGap = 2.0;
        double baseHGap = 2.5;

        for (int i = 0; i < nodes.size( ); i++) {
            T v = nodes.get(i).value;
            int depth = (int) (Math.log(i + 1) / Math.log(2));
            int pos = i - ((1 << depth) - 1);
            indexAtDepth.put(v, pos);
            depthMap.put(v, depth);
        }

        for (int d : depthMap.values( )) {
            countAtDepth.merge(d, 1, Integer::sum);
        }

        int maxDepth = java.util.Collections.max(depthMap.values( ));
        double totalSpan = baseHGap * (1 << maxDepth);

        for (Map.Entry<T, JBox> e : valueToBox.entrySet( )) {
            T v = e.getKey( );
            JBox box = e.getValue( );
            int d = depthMap.get(v);
            int pos = indexAtDepth.get(v);
            int count = countAtDepth.get(d);
            double gap = totalSpan / count;
            double hGap = baseHGap * (1 << d);
            double vGap = baseVGap * (1 << d);
            double x = ((count - 1) / 2.0 - pos) * gap;
            double y = -d * baseVGap;

            Point target = new Point(x, y, 0);
            box.center = target;
        }

        int dNew = depthMap.get(value);
        int pP2 = indexAtDepth.get(value);
        int count = countAtDepth.get(dNew);
        double gap = totalSpan / count;
        double x = ((count - 1) / 2.0 - pP2) * gap;
        double y = -dNew * baseVGap;
        Point pP = new Point(x, y, 0);
        cameraAnimator.slideTo(pP);

        JBox newBox = new JBox(
                pP, 0.75, 0.82, 0.1,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, String.valueOf(value), particle
        );

        world.add(newBox);
        valueToBox.put(value, newBox);
        nodeToBox.put(newNode, newBox);

        switch (animation) {
            case BOUNCE -> boxAnimator.bounceIn(newBox, pP.y);
            case SLIDE_FROM_TOP -> boxAnimator.slideFromTop(newBox, pP.y);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(newBox, pP.x);
            case SLIDE_FROM_RIGHT -> boxAnimator.slideFromRight(newBox, pP.x);
            case SCALE_POP -> boxAnimator.scalePop(newBox);
            case SHAKE -> boxAnimator.shake(newBox);
        }

        siftUp(newNode);

    }

    public void runRemoveAnimation() {
        if (root == null || heapSize == 0) return;

        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Removing");
        subtitle.setValue(root.value.toString());

        // Level-order list to find last node
        ArrayList<TreeNode> nodeList = new ArrayList<>();
        collectLevelOrder(root, nodeList);
        TreeNode lastNode = nodeList.get(nodeList.size() - 1);
        heapSize--;


        // Edge case: only one node
        TreeNode removedNode = root;
        if (removedNode == lastNode) {
            JBox removedBox = nodeToBox.get(removedNode);
            if (removedBox != null) {
                cameraAnimator.slideTo(removedBox.getCenter());
                boxAnimator.scaleDown(removedBox);
                world.remove(removedBox);
                nodeToBox.remove(removedNode);
                valueToBox.remove(removedNode.value);
                boxAnimator.highlight(removedBox);
            }
            root = null;
            return;
        }

        // Replace root's value with last node's value
        JBox rootBox = nodeToBox.get(root);
        JBox lastBox = nodeToBox.get(lastNode);
        if (rootBox != null && lastBox != null) {

            cameraAnimator.slideTo(lastBox.getCenter());
            boxAnimator.highlight(lastBox);
            cameraAnimator.slideTo(rootBox.getCenter());
            boxAnimator.highlight(rootBox);

            world.remove(lastBox);
            valueToBox.remove(lastNode.value);
            nodeToBox.remove(lastNode);

            valueToBox.remove(root.value);
            valueToBox.put(lastNode.value, rootBox);

            root.value = lastNode.value;
            rootBox.val = lastNode.value.toString();

            boxAnimator.scalePop(rootBox);
        }

        // Remove last node from tree structure
        TreeNode parent = findParent(root, lastNode);
        if (parent != null) {
            if (parent.left == lastNode) parent.left = null;
            else if (parent.right == lastNode) parent.right = null;
        }

        // Re-layout remaining tree
        ArrayList<TreeNode> nodes = new ArrayList<>();
        collectLevelOrder(root, nodes);

        Map<Integer, Integer> countAtDepth = new HashMap<>();
        Map<T, Integer> indexAtDepth = new HashMap<>();
        Map<T, Integer> depthMap = new HashMap<>();

        double baseVGap = 2.0;
        double baseHGap = 2.5;

        for (int i = 0; i < nodes.size(); i++) {
            T v = nodes.get(i).value;
            int depth = (int) (Math.log(i + 1) / Math.log(2));
            int pos = i - ((1 << depth) - 1);
            indexAtDepth.put(v, pos);
            depthMap.put(v, depth);
        }

        for (int d : depthMap.values()) {
            countAtDepth.merge(d, 1, Integer::sum);
        }

        int maxDepth = depthMap.values().stream().max(Integer::compareTo).orElse(0);
        double totalSpan = baseHGap * (1 << maxDepth);

        for (Map.Entry<T, JBox> e : valueToBox.entrySet()) {
            T v = e.getKey();
            JBox box = e.getValue();
            int d = depthMap.get(v);
            int pos = indexAtDepth.get(v);
            int count = countAtDepth.get(d);
            double gap = totalSpan / count;
            double x = ((count - 1) / 2.0 - pos) * gap;
            double y = -d * baseVGap;
            box.center = new Point(x, y, 0);
        }

        siftDown(root);
    }


  private void siftDown(TreeNode node) {
        while (true) {
            TreeNode left = node.left;
            TreeNode right = node.right;
            TreeNode largest = node;

            if (left != null && ((Comparable<T>) left.value).compareTo(largest.value) < 0) {
                largest = left;
            }
            if (right != null && ((Comparable<T>) right.value).compareTo(largest.value) < 0) {
                largest = right;
            }
            if (largest == node) break;

            JBox nodeBox = nodeToBox.get(node);
            JBox smallBox = nodeToBox.get(largest);
            if (nodeBox == null || smallBox == null) break;

            cameraAnimator.slideTo(smallBox.getCenter( ));
            boxAnimator.highlight(smallBox);
            cameraAnimator.slideTo(nodeBox.getCenter( ));
            boxAnimator.highlight(nodeBox);

            T tmp = node.value;
            node.value = largest.value;
            largest.value = tmp;

            nodeBox.val = node.value.toString( );
            smallBox.val = largest.value.toString( );

            valueToBox.put(node.value, nodeBox);
            valueToBox.put(largest.value, smallBox);

            boxAnimator.scalePop(smallBox);

            node = largest;
        }
    }

}