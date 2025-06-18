package Animations.Animator;

import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;
import Animations.Entrance;
import Animations.Exit;
import Animations.Traversal;
import Collections.JAVLTree;
import Rendering.*;
import Shapes.Core.Shape;
import Shapes.JBox;
import Utility.*;

import java.sql.SQLOutput;
import java.util.*;

public class JAVLTreesAnimator<T extends Comparable<T>> {
    private final ArrayList<Shape> world;
    private final Camera camera;
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
        public int height = 1;

        TreeNode(T value) {
            this.value = value;
        }
    }

    private int height(TreeNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(TreeNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private TreeNode rotateRight(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private static class RotationRecord<T> {
        T unbalancedNode;
        String type;
    }
    private RotationRecord<T> rotation = null;


    private TreeNode insert(TreeNode node, T value) {
        if (node == null) return new TreeNode(value);

        if (value.compareTo(node.value) < 0)
            node.left = insert(node.left, value);
        else if (value.compareTo(node.value) > 0)
            node.right = insert(node.right, value);
        else return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && value.compareTo(node.left.value) < 0) {
            rotation = new RotationRecord<>();
            rotation.unbalancedNode = node.value;
            rotation.type = "LL";
        } else if (balance < -1 && value.compareTo(node.right.value) > 0) {
            rotation = new RotationRecord<>();
            rotation.unbalancedNode = node.value;
            rotation.type = "RR";
        } else if (balance > 1 && value.compareTo(node.left.value) > 0) {
            rotation = new RotationRecord<>();
            rotation.unbalancedNode = node.value;
            rotation.type = "LR";
        } else if (balance < -1 && value.compareTo(node.right.value) < 0) {
            rotation = new RotationRecord<>();
            rotation.unbalancedNode = node.value;
            rotation.type = "RL";
        }

        return node;
    }



    private TreeNode delete(TreeNode node, T value) {
        if (node == null) return null;

        if (value.compareTo(node.value) < 0)
            node.left = delete(node.left, value);
        else if (value.compareTo(node.value) > 0)
            node.right = delete(node.right, value);
        else {
            if (node.left == null || node.right == null) {
                TreeNode temp = (node.left != null) ? node.left : node.right;
                if (temp == null) return null;
                node = temp;
            } else {
                TreeNode minLarger = getMin(node.right);
                node.value = minLarger.value;
                node.right = delete(node.right, minLarger.value);
            }
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0)
            return rotateRight(node);

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0)
            return rotateLeft(node);

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private TreeNode getMin(TreeNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public JAVLTreesAnimator() {
        this.scale = 0.5;
        this.positionAlongX = 0;
        this.framesPerSecond = 20;
        this.world = new ArrayList<>( );
        this.material = Texture.METAL;
        this.particle = Effect.NONE;
        this.camera = new Camera( );
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

        root = insert(root, value);

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

        cameraAnimator.slideTo(pNew);

        JBox newBox = new JBox(
                pNew, 0.75, 0.82, 0.1,
                new Color(0.4f,0.7f,1.0f),
                material, 0, String.valueOf(value), particle
        );

        subtitle.setMode("Inserting");
        subtitle.setValue(String.valueOf(value));

        world.add(newBox);
        valueToBox.put(value, newBox);

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

            JBox hBar = makeBar(
                    new Point(P.x, P.y, P.z),
                    new Point(C.x, P.y, C.z),
                    thickness,
                    null
            );
            world.add(hBar); worldBars.add(hBar);
            boxAnimator.scalePopFastest(hBar);

            JBox vBar = makeBar(
                    new Point(C.x, P.y, C.z),
                    new Point(C.x, C.y, C.z),
                    thickness,
                    null
            );
            world.add(vBar); worldBars.add(vBar);
            boxAnimator.scalePopFastest(vBar);

            if(i < cameraPathBoxes.size()) {
                JBox boxCam = cameraPathBoxes.get(i++);
                Point target = boxCam.getCenter();
                cameraAnimator.slideTo(target);
            }


        }

        if (rotation != null) {
            for (JBox bar : worldBars) {
                world.remove(bar);
            }
            worldBars.clear();
            T unbalanced = rotation.unbalancedNode;
            String type = rotation.type;

            JBox unbalancedBox = valueToBox.get(unbalanced);

            subtitle.setMode("Rebalancing");
            subtitle.setValue("AVL Rotation: " + type + " on " + unbalanced);

            TreeNode node = findNode(root, unbalanced);
            if (node == null) return;
            TreeNode A = node;
            TreeNode B = null, C = null;
            JBox ABox = valueToBox.get(A.value);
            JBox BBox, CBox;


            switch (type) {
                case "LL" -> {
                    B = node.left;
                    if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.left;
                    if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateLLRotation(ABox, BBox, CBox);
                }
                case "RR" -> {
                    B = node.right;
                    if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.right;
                    if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateRRRotation(ABox, BBox, CBox);
                }
                case "LR" -> {
                    B = node.left;
                    if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.right;
                    if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateLRRotation(ABox, BBox, CBox);

                    JBox temp = BBox;
                    BBox = CBox;
                    CBox = temp;

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateLLRotation(ABox, BBox, CBox);

                }
                case "RL" -> {
                    B = node.right;
                    if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.left;
                    if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateRLRotation(ABox, BBox, CBox);

                    JBox temp = BBox;
                    BBox = CBox;
                    CBox = temp;

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);

                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(BBox.getCenter());

                    boxAnimator.highlight(ABox);
                    cameraAnimator.slideTo(ABox.getCenter());

                    boxAnimator.animateRRRotation(ABox, BBox, CBox);

                }
            }

            switch (type) {
                case "LL" -> root = rotateAt(root, unbalanced, "LL");
                case "RR" -> root = rotateAt(root, unbalanced, "RR");
                case "LR" -> root = rotateAt(root, unbalanced, "LR");
                case "RL" -> root = rotateAt(root, unbalanced, "RL");
            }

            inorderIndex.clear();
            depthMap.clear();
            nextIndex = 0;
            assignIndices(root, 0);
            int rootIdxFinal = inorderIndex.get(root.value);

            for (Map.Entry<T, JBox> e : valueToBox.entrySet()) {
                T v = e.getKey();
                JBox box = e.getValue();
                int idx = inorderIndex.get(v);
                int d = depthMap.get(v);
                Point target = new Point((idx - rootIdxFinal) * 2.0, -d * 2.0, 0);
                boxAnimator.moveBoxTo(box, target);
                box.center = target;
            }

            rotation = null;
        }

    }

    private TreeNode rotateAt(TreeNode node, T target, String type) {
        if (node == null) return null;
        if (node.value.equals(target)) {
            return switch (type) {
                case "LL" -> rotateRight(node);
                case "RR" -> rotateLeft(node);
                case "LR" -> {
                    node.left = rotateLeft(node.left);
                    yield rotateRight(node);
                }
                case "RL" -> {
                    node.right = rotateRight(node.right);
                    yield rotateLeft(node);
                }
                default -> node;
            };
        }

        if (target.compareTo(node.value) < 0)
            node.left = rotateAt(node.left, target, type);
        else
            node.right = rotateAt(node.right, target, type);

        return node;
    }

    private TreeNode findNode(TreeNode node, T value) {
        if (node == null) return null;
        if (node.value.equals(value)) return node;
        if (value.compareTo(node.value) < 0)
            return findNode(node.left, value);
        else
            return findNode(node.right, value);
    }


    private final ArrayList<JBox> worldBars = new ArrayList<>();
    public void runRemoveAnimation(T value, Exit animation) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        if (!valueToBox.containsKey(value)) return;

        subtitle.setMode("Removing");
        subtitle.setValue(String.valueOf(value));

        JBox boxToRemove = valueToBox.get(value);
        cameraAnimator.slideTo(boxToRemove.getCenter());
        boxAnimator.highlight(boxToRemove);

        animateRemoval(boxToRemove, animation);

        world.remove(boxToRemove);
        valueToBox.remove(value);

        root = delete(root, value);

        inorderIndex.clear();
        depthMap.clear();
        nextIndex = 0;
        assignIndices(root, 0);

        int rootIdx = root != null ? inorderIndex.get(root.value) : 0;
        double hGap = 2.0;
        double vGap = 2.0;

        for (JBox bar : worldBars) world.remove(bar);
        worldBars.clear();

        for (Map.Entry<T, JBox> e : valueToBox.entrySet()) {
            T v = e.getKey();
            JBox box = e.getValue();
            int idx = inorderIndex.get(v);
            int d = depthMap.get(v);
            Point target = new Point((idx - rootIdx) * hGap, -d * vGap, 0);
            boxAnimator.moveBoxTo(box, target);
            box.center = target;
        }

        for (Map.Entry<T,JBox> entry : valueToBox.entrySet()) {
            T childVal = entry.getKey();
            TreeNode parentNode = findParent(root, childVal, null);
            if (parentNode == null) continue;

            JBox childBox  = entry.getValue();
            JBox parentBox = valueToBox.get(parentNode.value);
            Point P = parentBox.getCenter();
            Point C = childBox .getCenter();

            JBox hBar = makeBar(new Point(P.x, P.y, P.z), new Point(C.x, P.y, C.z), 0.12, null);
            world.add(hBar); worldBars.add(hBar);
            boxAnimator.scalePopFastest(hBar);

            JBox vBar = makeBar(new Point(C.x, P.y, C.z), new Point(C.x, C.y, C.z), 0.12, null);
            world.add(vBar); worldBars.add(vBar);
            boxAnimator.scalePopFastest(vBar);


        }

        if (rotation != null) {

            for (JBox bar : worldBars) {
                world.remove(bar);
            }
            worldBars.clear();

            T unbalanced = rotation.unbalancedNode;
            String type = rotation.type;
            JBox unbalancedBox = valueToBox.get(unbalanced);

            subtitle.setMode("Rebalancing");
            subtitle.setValue("AVL Rotation: " + type + " on " + unbalanced);

            TreeNode node = findNode(root, unbalanced);
            if (node == null) return;
            TreeNode A = node;
            TreeNode B = null, C = null;
            JBox ABox = valueToBox.get(A.value);
            JBox BBox, CBox;

            switch (type) {
                case "LL" -> {
                    B = node.left; if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.left; if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateLLRotation(ABox, BBox, CBox);
                }
                case "RR" -> {
                    B = node.right; if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.right; if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateRRRotation(ABox, BBox, CBox);
                }
                case "LR" -> {
                    B = node.left; if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.right; if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateLRRotation(ABox, BBox, CBox);
                    JBox temp = BBox; BBox = CBox; CBox = temp;

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateLLRotation(ABox, BBox, CBox);
                }
                case "RL" -> {
                    B = node.right; if (B == null) return;
                    BBox = valueToBox.get(B.value);
                    C = B.left; if (C == null) return;
                    CBox = valueToBox.get(C.value);

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateRLRotation(ABox, BBox, CBox);
                    JBox temp = BBox; BBox = CBox; CBox = temp;

                    cameraAnimator.slideTo(CBox.getCenter());
                    boxAnimator.highlight(CBox);
                    cameraAnimator.slideTo(BBox.getCenter());
                    boxAnimator.highlight(BBox);
                    cameraAnimator.slideTo(ABox.getCenter());
                    boxAnimator.highlight(ABox);

                    boxAnimator.animateRRRotation(ABox, BBox, CBox);
                }
            }

            root = rotateAt(root, unbalanced, type);

            inorderIndex.clear();
            depthMap.clear();
            nextIndex = 0;
            assignIndices(root, 0);
            int rootIdxFinal = inorderIndex.get(root.value);

            for (Map.Entry<T, JBox> e : valueToBox.entrySet()) {
                T v = e.getKey();
                JBox box = e.getValue();
                int idx = inorderIndex.get(v);
                int d = depthMap.get(v);
                Point target = new Point((idx - rootIdxFinal) * 2.0, -d * 2.0, 0);
                cameraAnimator.slideTo(target);
                boxAnimator.moveBoxTo(box, target);
                box.center = target;
            }

            rotation = null;
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

    public StringBuilder runTraversalAnimation(JAVLTree<T>.TreeNode node, Traversal order) {
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

    private void collectInorder(JAVLTree<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        collectInorder(node.left, path);
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
        collectInorder(node.right, path);
    }

    private void collectPreorder(JAVLTree<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
        collectPreorder(node.left, path);
        collectPreorder(node.right, path);
    }

    private void collectPostorder(JAVLTree<T>.TreeNode node, ArrayList<JBox> path) {
        if (node == null) return;
        collectPostorder(node.left, path);
        collectPostorder(node.right, path);
        JBox box = valueToBox.get(node.value);
        if (box != null) path.add(box);
    }

    public boolean runSearchAnimation(JAVLTree<T>.TreeNode node, T value) {
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

    public int runHeightAnimation(JAVLTree<T>.TreeNode node) {
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

    private int computeHeightAnimated(JAVLTree<T>.TreeNode node) {
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

    public T runMinMaxAnimation(JAVLTree<T>.TreeNode node, boolean findMin) {
        if (randomBackground) setRandomBackground();
        subtitle.setMode(findMin ? "Finding Min" : "Finding Max");
        subtitle.setValue("");
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JAVLTree<T>.TreeNode cur = node;
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

    public String runLeafHighlightAnimation(JAVLTree<T>.TreeNode node) {
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

    private StringBuilder highlightLeavesRecursive(JAVLTree<T>.TreeNode node, StringBuilder leafNodesCapture) {
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
