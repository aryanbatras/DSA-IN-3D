package Collections;

import Algorithms.AVLTrees;
import Animations.Animator.JAVLTreesAnimator;
import Algorithms.Trees;
import Animations.Dynamo;
import Animations.Entrance;
import Animations.Exit;
import Animations.Traversal;
import Rendering.*;
import Utility.Code;
import Utility.Encoder;
import Utility.Variable;
import Utility.Window;

import java.util.HashSet;
import java.util.Set;

public class JAVLTree<T extends Comparable<T>> {

    private TreeNode root;
    private final JAVLTreesAnimator<T> animator;

    public class TreeNode {
         public T value;
         public TreeNode left;
         public TreeNode right;
         public int height = 1;
        TreeNode(T value) {
            this.value = value;
        }
    }

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private AVLTrees algo;
    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JAVLTree() {
        this.root = null;
        this.scale = 0.25;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.animator = new JAVLTreesAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SHRINK_AND_DROP;
        animator.setScale(0.25);
        animator.setFPS(1);
        this.randomizer = null;
        this.built = true;
    }

    public JAVLTree withAlgoVisualizer(AVLTrees algo){
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
        algo.run(this);
    }

    public JAVLTree withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JAVLTree withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JAVLTree withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JAVLTree withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JAVLTree withQuality(Resolution quality) {
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

    public JAVLTree withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JAVLTree withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JAVLTree withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JAVLTree withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JAVLTree withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JAVLTree withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JAVLTree withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JAVLTree withAntiAliasing(Smooth antiAliasing) {
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

    public JAVLTree withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JAVLTree withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JAVLTree withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JAVLTree build() {

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

    private TreeNode insert(TreeNode node, T value) {
        if (node == null) return new TreeNode(value);

        if (value.compareTo(node.value) < 0)
            node.left = insert(node.left, value);
        else if (value.compareTo(node.value) > 0)
            node.right = insert(node.right, value);
        else
            return node; // no duplicates

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // LL
        if (balance > 1 && value.compareTo(node.left.value) < 0)
            return rotateRight(node);

        // RR
        if (balance < -1 && value.compareTo(node.right.value) > 0)
            return rotateLeft(node);

        // LR
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RL
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
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

        // LL
        if (balance > 1 && getBalance(node.left) >= 0)
            return rotateRight(node);

        // LR
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RR
        if (balance < -1 && getBalance(node.right) <= 0)
            return rotateLeft(node);

        // RL
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

    public void add(T value) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        root = insert(root, value);

            animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance);

    }

    public void add(T value, Entrance animation) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        root = insert(root, value);

            animator.runAddAnimation(value, animation);

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

            animator.runRemoveAnimation(value, randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit);


        root = delete(root, value);
        Variable.update("remove", value);
        return value;
    }

    public T remove(T value, Exit animation) {
        Code.markCurrentLine();
        checkBuilt();

            animator.runRemoveAnimation(value, animation);


        root = delete(root, value);
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
