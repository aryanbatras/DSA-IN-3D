package Collections;

import Algorithms.Trees;
import Animations.Dynamo;
import Animations.Animator.JTreesAnimator;

import java.util.*;

import Rendering.View;
import Animations.*;
import Rendering.*;
import Utility.*;

public class JTrees<T extends Comparable<T>> {

    private TreeNode root;
    private final JTreesAnimator<T> animator;

    public class TreeNode {
         public T value;
         public TreeNode left;
         public TreeNode right;
        TreeNode(T value) {
            this.value = value;
        }
    }

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private Trees algo;
    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JTrees() {
        this.root = null;
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.animator = new JTreesAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SLIDE_UP;
        this.randomizer = null;
        this.built = true;
    }

    public JTrees withAlgoVisualizer(Trees algo){
        this.algo = algo;
        /*
         you have to build functions to
         sort the internal array as per algo
         because what algo handles is external array
         */
        this.built = false;
        return this;
    }

    public void run() {
        if (algo == null) { throw new IllegalStateException(" No algo was given via .withAlgoVisualizer() "); }
        if (this.isEmpty()) { throw new IllegalStateException(" Tree is empty "); }
//        algo.run(this);
    }

    public JTrees withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JTrees withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JTrees withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JTrees withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JTrees withQuality(Resolution quality) {
        switch (quality) {
            case BEST -> scale = 1.0;
            case GOOD -> scale = 0.75;
            case BALANCE -> scale = 0.5;
            case FASTEST -> scale = 0.25;
        }
        animator.setScale(scale);
        explicitlySetProperties.add("quality");
        this.built = false;
        return this;
    }

    public JTrees withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JTrees withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JTrees withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JTrees withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JTrees withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JTrees withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JTrees withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JTrees withAntiAliasing(Smooth antiAliasing) {
        double alias = 0;
        switch (antiAliasing) {
            case NONE -> alias = 1.0;
            case X2 -> alias = 2.0;
            case X4 -> alias = 4.0;
            case X8 -> alias = 8.0;
        }
        animator.setAntiAliasing(alias);
        this.built = false;
        return this;
    }

    public JTrees withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JTrees withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JTrees withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JTrees build() {

        if (randomizer != null) {
            if (randomizer.shouldRandomizeInsertAnimation()  && !explicitlySetProperties.contains("insertAnimation")) {
                this.defaultEntrance = Dynamo.randomInsertAnimation();
            }
            if (randomizer.shouldRandomizeRemoveAnimation()  && !explicitlySetProperties.contains("removeAnimation")) {
                this.defaultExit = Dynamo.randomRemoveAnimation();
            }
            if (randomizer.shouldRandomizeRenderMode() && !explicitlySetProperties.contains("renderMode")) {
                withRenderMode(Dynamo.randomRenderMode());
            }
            if (randomizer.shouldRandomizeQuality() && !explicitlySetProperties.contains("quality")) {
                withQuality(Dynamo.randomQuality());
            }
            if (randomizer.shouldRandomizeMaterial() && !explicitlySetProperties.contains("material")) {
                withMaterial(Dynamo.randomMaterial());
            }
            if (randomizer.shouldRandomizeBackground() && !explicitlySetProperties.contains("background")) {
                withBackground(Dynamo.randomBackground());
            }
            if (randomizer.shouldRandomizeParticle() && !explicitlySetProperties.contains("particle")) {
                withParticle(Dynamo.randomParticle());
            }
            if (randomizer.shouldRandomizeSteps() && !explicitlySetProperties.contains("steps")) {
                withStepsPerAnimation(Dynamo.randomSteps());
            }
            if (randomizer.shouldRandomizeCameraRotation() && !explicitlySetProperties.contains("cameraRotation")) {
                withCameraRotations(Dynamo.randomCameraRotation());
            }
            if (randomizer.shouldRandomizeCameraSpeed() && !explicitlySetProperties.contains("cameraSpeed")) {
                withCameraSpeed(Dynamo.randomCameraSpeed());
            }
        }

        if (mode == Render.VIDEO && userProvidedOutput) {
            if (preferSharedEncoder) {
                encoder = Encoder.getOrCreateNamedEncoder(userOutput, scale);
            } else {
                encoder = Encoder.initializeEncoder(userOutput, scale);
            }
            animator.setEncoder(encoder);
        }

        if (mode == Render.VIDEO && !userProvidedOutput) {
            if (preferSharedEncoder) {
                encoder = Encoder.getOrCreateSharedEncoder(scale);
            } else {
                encoder = Encoder.initializeEncoder(scale);
            }
            animator.setEncoder(encoder);
        }

        if (mode == Render.LIVE || mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.initializeWindow();
            if (mode == Render.STEP_WISE_INTERACTIVE) {
                Window.setupInteractivity();
            }
        }

        this.built = true;
        return this;
    }

    private void checkBuilt() {
        if (!built) {
            throw new IllegalStateException("JTrees not built! Call .build() before use.");
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

    public void add(T value) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        root = insertIntoBST(root, value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance);
        }
    }

    public void add(T value, Entrance animation) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        root = insertIntoBST(root, value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, animation);
        }
    }

    public T delete(T value){
     return remove(value);
    }

    public T delete(T value, Exit animation){
        return remove(value);
    }

    public T remove(T value) {
        Code.markCurrentLine();
        checkBuilt();

        if (mode != Render.DISABLED) {
            animator.runRemoveAnimation(value, randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit);
        }

        root = deleteFromBST(root, value);
        Variable.update("remove", value);
        return value;
    }

    public T remove(T value, Exit animation) {
        Code.markCurrentLine();
        checkBuilt();

        if (mode != Render.DISABLED) {
            animator.runRemoveAnimation(value, animation);
        }

        root = deleteFromBST(root, value);
        Variable.update("remove", value);
        return value;
    }

    public int size() {
        Code.markCurrentLine();
        checkBuilt();
        return getSize(root);
    }

    private int getSize(TreeNode node) {
        if (node == null) return 0;
        return 1 + getSize(node.left) + getSize(node.right);
    }

    public boolean isEmpty() {
        Code.markCurrentLine();
        checkBuilt();
        return root == null;
    }

    public void clear() {
        root = null;
    }

//    public boolean contains(T value) {
//        return containsNode(root, value);
//    }
//    private boolean containsNode(TreeNode node, T value) {
//        if (node == null) return false;
//        int cmp = value.compareTo(node.value);
//        if (cmp == 0) return true;
//        return cmp < 0 ? containsNode(node.left, value) : containsNode(node.right, value);
//    }
//
//    public T findMin() {
//        if (root == null) return null;
//        return getMin(root).value;
//    }
//
//    public T findMax() {
//        TreeNode node = root;
//        while (node.right != null) node = node.right;
//        return node.value;
//    }

//    public int getHeight() {
//        return getHeight(root);
//    }
//    private int getHeight(TreeNode node) {
//        if (node == null) return -1;
//        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
//    }

//    public List<T> inorder() {
//        List<T> result = new ArrayList<>();
//        inorderTraversal(root, result);
//        return result;
//    }
//    private void inorderTraversal(TreeNode node, List<T> result) {
//        if (node != null) {
//            inorderTraversal(node.left, result);
//            result.add(node.value);
//            inorderTraversal(node.right, result);
//        }
//    }
//
////    public List<T> preorder() {
////        List<T> result = new ArrayList<>();
////        preorderTraversal(root, result);
////        return result;
////    }
//    private void preorderTraversal(TreeNode node, List<T> result) {
//        if (node != null) {
//            result.add(node.value);
//            preorderTraversal(node.left, result);
//            preorderTraversal(node.right, result);
//        }
//    }
//
////    public List<T> postorder() {
////        List<T> result = new ArrayList<>();
////        postorderTraversal(root, result);
////        return result;
////    }
//    private void postorderTraversal(TreeNode node, List<T> result) {
//        if (node != null) {
//            postorderTraversal(node.left, result);
//            postorderTraversal(node.right, result);
//            result.add(node.value);
//        }
//    }

    public String inorder() {
        Code.markCurrentLine();
        checkBuilt();

       return "Inorder Traversal -> " + animator.runTraversalAnimation(root, Traversal.INORDER).toString();
    }

    public String preorder() {
        Code.markCurrentLine();
        checkBuilt();

        return "Preorder Traversal -> " +  animator.runTraversalAnimation(root, Traversal.PREORDER).toString();
    }

    public String postorder() {
        Code.markCurrentLine();
        checkBuilt();

        return "Postorder Traversal -> " +  animator.runTraversalAnimation(root, Traversal.POSTORDER).toString();
    }

    public String leaves() {
        Code.markCurrentLine();
        checkBuilt();

        return "Leaf Nodes -> " + animator.runLeafHighlightAnimation(root);
    }

    public boolean contains(T value) {
    return search(value);
    }

    public boolean search(T value) {
        Code.markCurrentLine();
        checkBuilt();

        return animator.runSearchAnimation(root, value);
    }

    public int getHeight() {
        Code.markCurrentLine();
        checkBuilt();

        return animator.runHeightAnimation(root);
    }

    public T getMin() {
        Code.markCurrentLine();
        checkBuilt();

        return animator.runMinMaxAnimation(root, true);
    }

    public T getMax() {
        Code.markCurrentLine();
        checkBuilt();

        return animator.runMinMaxAnimation(root, false);
    }



}
