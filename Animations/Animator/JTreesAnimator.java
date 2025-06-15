package Animations.Animator;

import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Collections.JTrees;
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

public class JTreesAnimator<T extends Comparable<T>> {
    private final ArrayList<Shape> world;
    private final Utility.Camera camera;
    private final Renderer renderer;
    private Subtitle subtitle;

    private int positionAlongX;
    private int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Random rand;
    private Render mode;
    private double scale;
    private Texture material;
    private String background;
    private Effect particle;
    private boolean randomBackground;
    private double hidePointerFirstGlance;

    private Map<T, JBox> valueToBox = new HashMap<>( );

    private TreeNode root;


    private class TreeNode {
        T value;
        TreeNode left;
        TreeNode right;

        TreeNode(T value) {
            this.value = value;
        }
    }

    private TreeNode insertIntoBST(TreeNode node, T value) {
        if (node == null) return new TreeNode(value);
        if (value.compareTo(node.value) < 0) {
            node.left = insertIntoBST(node.left, value);
        } else {
            node.right = insertIntoBST(node.right, value);
        }
        return node;
    }

    private TreeNode deleteFromBST(TreeNode node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp < 0) node.left = deleteFromBST(node.left, value);
        else if (cmp > 0) node.right = deleteFromBST(node.right, value);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            TreeNode minLarger = getMin(node.right);
            node.value = minLarger.value;
            node.right = deleteFromBST(node.right, minLarger.value);
        }
        return node;
    }

    private TreeNode getMin(TreeNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public JTreesAnimator() {
        this.scale = 0.5;
        this.positionAlongX = 0;
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
        hidePointerFirstGlance = 0.0;
        this.mode = Render.DISABLED;
        this.rand = new Random( );
    }

    public JBox setGoldPointer() {
        JBox pointer = new JBox(
                new Point(0, 0, 0.1),
                0, 0.42, 0.1,
                new Color(1.0f, 0.82f, 0.0f),
                Texture.METAL, 0.0f, null,
                particle
        );
        return pointer;
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
        Scenery randomBg = Scenery.values( )[rand.nextInt(Scenery.values( ).length)];
        setBackground(randomBg.toString( ));
    }

    public void setRandomizeBackgroundAsTrue() {
        this.randomBackground = true;
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius( ) - focus);
    }

    private Map<T,Integer> inorderIndex = new HashMap<>();
    private Map<T,Integer> depthMap    = new HashMap<>();
    private int nextIndex;

    private void assignIndices(TreeNode node, int depth) {
        if (node == null) return;
        assignIndices(node.left,  depth + 1);
        inorderIndex.put(node.value, nextIndex--);
        depthMap.put(node.value, depth);
        assignIndices(node.right, depth + 1);
    }

    private Point calculateNodePosition(TreeNode node, T value, double x, double y) {
        if (node == null) {
            return new Point(x, y, 0);
        }
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            return new Point(x, y, 0);
        }
        if (cmp < 0) {
            return calculateNodePosition(node.left, value, x + 1, y - 1);
        } else {
            return calculateNodePosition(node.right, value, x - 1, y - 1);
        }
    }

    private TreeNode findParent(TreeNode current, T childValue, TreeNode parent) {
        if (current == null) return null;
        if (current.value.equals(childValue)) return parent;
        if (childValue.compareTo(current.value) < 0)
            return findParent(current.left, childValue, current);
        else
            return findParent(current.right, childValue, current);
    }

    public void runAddAnimation(T value, Entrance animation) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        root = insertIntoBST(root, value);

        inorderIndex.clear();
        depthMap.clear();
        nextIndex = 0;
        assignIndices(root, 0);

        int rootIdx = inorderIndex.get(root.value);

        double hGap = 2.0;
        double vGap = 2.0;
        boolean shiftRequired = false;

        for (Map.Entry<T,JBox> e : valueToBox.entrySet()) {
            T   v    = e.getKey();
            JBox box = e.getValue();

            int idx   = inorderIndex.get(v);
            int d     = depthMap.get(v);

            double x = (idx - rootIdx) * hGap;
            double y = -d * vGap;

            if (box.center.x != x && shiftRequired == false) {
                shiftRequired = true;
                for (JBox bar : worldBars) {
                    world.remove(bar);
                }
                worldBars.clear();
            }

            Point target = new Point(x, y, 0);
            boxAnimator.moveBoxTo(box, target);
            box.center = new Point(x, y, 0);
        }

        int   idxNew = inorderIndex.get(value);
        int   dNew   = depthMap.get(value);
        Point pNew   = new Point((idxNew - rootIdx)*hGap, -dNew*vGap, 0);

        cameraAnimator.slideTo(pNew);

        ArrayList<JBox> cameraPathBoxes = new ArrayList<>();
        TreeNode cur = root;
        while (cur != null) {
            JBox box = valueToBox.get(cur.value);
            if (box != null) cameraPathBoxes.add(box);

            int cmp = value.compareTo(cur.value);
            if      (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else               break;
        }

        JBox newBox = new JBox(
                pNew, 0.75, 0.82, 0.1,
                new Color(0.4f,0.7f,1.0f),
                material, 0, String.valueOf(value), particle
        );

        world.add(newBox);
        valueToBox.put(value, newBox);

        subtitle.setMode("Inserting");
        subtitle.setValue(String.valueOf(value));

        switch (animation) {
            case BOUNCE          -> boxAnimator.bounceIn(newBox, pNew.y);
            case SLIDE_FROM_TOP  -> boxAnimator.slideFromTop(newBox, pNew.y);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(newBox, pNew.x);
            case SLIDE_FROM_RIGHT-> boxAnimator.slideFromRight(newBox, pNew.x);
            case SCALE_POP       -> boxAnimator.scalePop(newBox);
            case SHAKE           -> boxAnimator.shake(newBox);
        }
        cameraPathBoxes.add(newBox);
        int i = 0;

        double thickness = 0.12;
        for (Map.Entry<T,JBox> entry : valueToBox.entrySet()) {
            T childVal = entry.getKey();
            TreeNode parentNode = findParent(root, childVal, null);
            if (parentNode == null) continue;

            JBox childBox  = entry.getValue();
            JBox parentBox = valueToBox.get(parentNode.value);
            Point P = parentBox.getCenter();
            Point C = childBox .getCenter();

            if(i < cameraPathBoxes.size()) {
                JBox boxCam = cameraPathBoxes.get(i++);
                Point target = boxCam.getCenter();
                cameraAnimator.slideTo(target);
                boxAnimator.scalePop(boxCam);
            }

            JBox hBar = makeBar(
                    new Point(P.x, P.y, P.z),
                    new Point(C.x, P.y, C.z),
                    thickness,
                    " " + parentNode.value
            );
            world.add(hBar); worldBars.add(hBar);
            boxAnimator.scalePopFast(hBar);

            JBox vBar = makeBar(
                    new Point(C.x, P.y, C.z),
                    new Point(C.x, C.y, C.z),
                    thickness,
                     " " + childVal
            );
            world.add(vBar); worldBars.add(vBar);
            boxAnimator.scalePopFast(vBar);

        }

    }

    private final ArrayList<JBox> worldBars = new ArrayList<>();

    public void runRemoveAnimation( T value,  Exit animation) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        TreeNode cur = root;
        ArrayList<JBox> cameraPathBoxes = new ArrayList<>();
        while (cur != null) {
            JBox b = valueToBox.get(cur.value);
            if (b != null) cameraPathBoxes.add(b);
            int cmp = value.compareTo(cur.value);
            if      (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else               break;
        }

        subtitle.setMode("Removing");
        subtitle.setValue(String.valueOf(value));
        for (JBox b : cameraPathBoxes) {
            cameraAnimator.slideTo(b.getCenter());
            boxAnimator.scalePop(b);
        }

        JBox targetBox = valueToBox.get(value);
        if (targetBox != null) {
            animateRemoval(targetBox, animation);
            world.remove(targetBox);
            valueToBox.remove(value);
        }

        root = deleteFromBST(root, value);
        for (JBox bar : worldBars) world.remove(bar);
        worldBars.clear();

        inorderIndex.clear();
        depthMap.clear();
        nextIndex = 0;
        assignIndices(root, 0);
        int rootIdx = inorderIndex.get(root.value);
        double hGap = 2.0, vGap = 2.0;

        for (Map.Entry<T,JBox> e : valueToBox.entrySet()) {
            T v    = e.getKey();
            JBox b = e.getValue();
            int idx = inorderIndex.get(v), d = depthMap.get(v);
            Point tgt = new Point((idx - rootIdx)*hGap, -d*vGap, 0);
            boxAnimator.moveBoxTo(b, tgt);
            b.setCenter(tgt);
        }

        double thickness = 0.12;
        for (Map.Entry<T,JBox> e : valueToBox.entrySet()) {
            T childVal = e.getKey();
            TreeNode parentNode = findParent(root, childVal, null);
            if (parentNode == null) continue;

            JBox childBox  = e.getValue();
            JBox parentBox = valueToBox.get(parentNode.value);
            Point P = parentBox.getCenter();
            Point C = childBox .getCenter();

            JBox hBar = makeBar(new Point(P.x,P.y,P.z), new Point(C.x,P.y,C.z), thickness, "");
            world.add(hBar); worldBars.add(hBar);
            boxAnimator.scalePopFast(hBar);

            JBox vBar = makeBar(new Point(C.x,P.y,C.z), new Point(C.x,C.y,C.z), thickness, "");
            world.add(vBar); worldBars.add(vBar);
            boxAnimator.scalePopFast(vBar);
        }

    }

    private JBox makeBar(Point from, Point to, double thickness, String name) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;

        boolean horizontal = Math.abs(dx) > Math.abs(dy);

        double length = horizontal ? Math.abs(dx) : Math.abs(dy);
        double cx = (from.x + to.x) / 2;
        double cy = (from.y + to.y) / 2;
        double cz = (from.z + to.z) / 2;

        double w = horizontal ? length : thickness;
        double h = horizontal ? thickness : length;
        double d = thickness;

        JBox bar = new JBox(
                new Point(cx, cy, cz + 0.01),
                w, h, d,
                new Color(1.0f, 0.82f, 0.0f),
                Texture.METAL,
                0.0, name, Effect.NONE
        );
        return bar;
    }

    private void animateRemoval(JBox box, Exit animation) {
        Point p = box.getCenter( );
        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(box, p.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(box, p.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(box);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(box);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(box);
        }
    }

    public StringBuilder runTraversalAnimation(JTrees<T>.TreeNode node, Traversal order) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode("Traversal");
        subtitle.setValue(order.name());
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        StringBuilder traversed = new StringBuilder();
        ArrayList<JBox> path = new ArrayList<>();
        switch (order) {
            case INORDER   -> collectInorder(node, path);
            case PREORDER  -> collectPreorder(node, path);
            case POSTORDER -> collectPostorder(node, path);
        }

        if (mode != Render.DISABLED) {
            for (JBox box : path) {
                cameraAnimator.slideTo(box.getCenter( ));
                boxAnimator.highlight(box);
                boxAnimator.shakeSlow(box);
                traversed.append(box.val).append(" ");
            }
        }

        return traversed;
    }

    private void collectInorder(JTrees<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        collectInorder(node.left, path);
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
        collectInorder(node.right, path);
    }

    private void collectPreorder(JTrees<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
        collectPreorder(node.left, path);
        collectPreorder(node.right, path);
    }

    private void collectPostorder(JTrees<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        collectPostorder(node.left, path);
        collectPostorder(node.right, path);
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
    }

    public boolean runSearchAnimation(JTrees<T>.TreeNode node, T value) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode("Searching");
        subtitle.setValue(String.valueOf(value));
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        while (node != null) {
            JBox box = valueToBox.get(node.value);
            if (box != null && mode != Render.DISABLED) {
                cameraAnimator.slideTo(box.getCenter());
                boxAnimator.highlight(box);
            }

            int cmp = value.compareTo(node.value);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return true;
        }

        return false;
    }

    public int runHeightAnimation(JTrees<T>.TreeNode node) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode("Computing Height");
        subtitle.setValue("");
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        return computeHeightAnimated(node);
    }

    private int computeHeightAnimated(JTrees<T>.TreeNode node) {
        if (node == null) return 0;

        JBox box = valueToBox.get(node.value);
        if (box != null && (mode != Render.DISABLED)) {
            cameraAnimator.slideTo(box.getCenter());
            boxAnimator.highlight(box);
        }

        int left = computeHeightAnimated(node.left);
        int right = computeHeightAnimated(node.right);

        return 1 + Math.max(left, right);
    }

    public T runMinMaxAnimation(JTrees<T>.TreeNode node, boolean findMin) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode(findMin ? "Finding Min" : "Finding Max");
        subtitle.setValue("");
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JTrees<T>.TreeNode cur = node;
        while ((findMin ? cur.left : cur.right) != null) {
            JBox box = valueToBox.get(cur.value);
            if (box != null && (mode != Render.DISABLED)) {
                cameraAnimator.slideTo(box.getCenter());
                boxAnimator.highlight(box);
            }
            cur = findMin ? cur.left : cur.right;
        }

        if (cur != null) {
            JBox last = valueToBox.get(cur.value);
            if (last != null && (mode != Render.DISABLED)) {
                cameraAnimator.slideTo(last.getCenter());
                boxAnimator.scalePop(last);
            }
            return cur.value;
        }

        return null;
    }

    public String runLeafHighlightAnimation(JTrees<T>.TreeNode node) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode("Highlighting Leaves");
        subtitle.setValue("");
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        StringBuilder leafNodesCapture = new StringBuilder();
        return highlightLeavesRecursive(node, leafNodesCapture).toString();
    }

    private StringBuilder highlightLeavesRecursive(JTrees<T>.TreeNode node, StringBuilder leafNodesCapture) {
        if (node == null) return leafNodesCapture;
        if (node.left == null && node.right == null) {
            JBox box = valueToBox.get(node.value);
            leafNodesCapture.append(node.value).append(" ");
            if (box != null && (mode != Render.DISABLED)) {
                cameraAnimator.slideTo(box.getCenter());
                boxAnimator.scalePop(box);
            }
        }

        highlightLeavesRecursive(node.left, leafNodesCapture);
        highlightLeavesRecursive(node.right, leafNodesCapture);
        return leafNodesCapture;
    }



}
