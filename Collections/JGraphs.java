package Collections;

import Algorithms.Trees;
import Animations.Animator.JGraphsAnimator;
import Animations.*;
import Rendering.*;
import java.util.*;
import Utility.*;

public class JGraphs<T> {

    private final Map<T, List<T>> adjacencyList;
    private final JGraphsAnimator<T> animator;

    // Configuration fields
    private double scale;
    private Encoder encoder;
    private Dynamo randomizer;
    private boolean built;
    private final Set<String> explicitlySetProperties;

    // Default animations
    private Entrance defaultVertexEntrance;
    private Exit defaultVertexExit;
    private Entrance defaultEdgeEntrance;
    private Exit defaultEdgeExit;

    // Render mode, output, etc
    private Render mode;
    private Trees algo;
    private String userOutput;
    private boolean userProvidedOutput;
    private boolean preferSharedEncoder;

    public JGraphs() {
        this.adjacencyList = new LinkedHashMap<>();
        this.animator = new JGraphsAnimator<>();
        this.scale = 0.5;
        this.mode = Render.DISABLED;
        this.encoder = null;
        this.randomizer = null;
        this.built = true;
        this.explicitlySetProperties = new HashSet<>();
        this.defaultVertexEntrance = Entrance.BOUNCE;
        this.defaultVertexExit = Exit.SCALE_DOWN;
        this.defaultEdgeEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultEdgeExit = Exit.SLIDE_UP;
    }
    public JGraphs  withAlgoVisualizer(Trees algo){
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
//        if (this.isEmpty()) { throw new IllegalStateException(" Tree is empty "); }
//        algo.run(this);
    }

    public JGraphs  withInsertAnimation(Entrance entrance) {
        this.defaultVertexEntrance = entrance;
        this.defaultEdgeEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JGraphs  withRemoveAnimation(Exit exit) {
        this.defaultEdgeExit = exit;
        this.defaultVertexExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JGraphs  withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JGraphs  withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JGraphs  withQuality(Resolution quality) {
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

    public JGraphs  withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JGraphs  withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JGraphs  withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JGraphs  withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JGraphs  withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JGraphs  withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JGraphs  withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JGraphs  withAntiAliasing(Smooth antiAliasing) {
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

    public JGraphs  withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JGraphs  withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JGraphs  withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JGraphs  build() {

        if (randomizer != null) {
            if (randomizer.shouldRandomizeInsertAnimation()  && !explicitlySetProperties.contains("insertAnimation")) {
                this.defaultVertexEntrance = Dynamo.randomInsertAnimation();
                this.defaultEdgeEntrance = Dynamo.randomInsertAnimation();
            }
            if (randomizer.shouldRandomizeRemoveAnimation()  && !explicitlySetProperties.contains("removeAnimation")) {
                this.defaultEdgeExit = Dynamo.randomRemoveAnimation();
                this.defaultVertexExit = Dynamo.randomRemoveAnimation();
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
            throw new IllegalStateException("JGraphs  not built! Call .build() before use.");
        }
    }

    // Vertex operations
    public void addVertex(T v) {
        Code.markCurrentLine();
        checkBuilt();
        adjacencyList.putIfAbsent(v, new ArrayList<>());
        if (mode != Render.DISABLED) {
            animator.runAddVertexAnimation(v,
                    randomizer != null ? randomizer.randomInsertAnimation() : defaultVertexEntrance);
        }
    }

    public T removeVertex(T v) {
        Code.markCurrentLine();
        checkBuilt();
        if (mode != Render.DISABLED) {
            animator.runRemoveVertexAnimation(v,
                    randomizer != null ? randomizer.randomRemoveAnimation() : defaultVertexExit);
        }
        adjacencyList.remove(v);
        // also remove edges
        for (List<T> nbrs : adjacencyList.values()) nbrs.remove(v);
        return v;
    }

    // Edge operations
    public void addEdge(T u, T v) {
        Code.markCurrentLine();
        checkBuilt();
        List<T> nbrs = adjacencyList.get(u);
        if (nbrs != null && nbrs.contains(v)) {
            return;  // already drawn this exact directed edge
        }
        // only insert & animate if new
        if (!adjacencyList.containsKey(u)) {
            adjacencyList.put(u, new ArrayList<>());
            if (mode != Render.DISABLED) animator.runAddVertexAnimation(u, defaultVertexEntrance);
        }
        if (!adjacencyList.containsKey(v)) {
            adjacencyList.put(v, new ArrayList<>());
            if (mode != Render.DISABLED) animator.runAddVertexAnimation(v, defaultVertexEntrance);
        }

        boolean reverseExists = adjacencyList.containsKey(v) && adjacencyList.get(v).contains(u);
        adjacencyList.get(u).add(v);

        if (mode != Render.DISABLED) {
            animator.runAddEdgeAnimation(u, v, randomizer != null ? randomizer.randomInsertAnimation() : defaultEdgeEntrance,
                    reverseExists);
        }
    }


    public void removeEdge(T u, T v) {
        Code.markCurrentLine();
        checkBuilt();
        if (mode != Render.DISABLED) {
            animator.runRemoveEdgeAnimation(u, v,
                    randomizer != null ? randomizer.randomRemoveAnimation() : defaultEdgeExit);
        }
        List<T> nbrs = adjacencyList.get(u);
        if (nbrs != null) nbrs.remove(v);
    }

    // Graph queries
    public boolean containsVertex(T v) {
        Code.markCurrentLine(); checkBuilt();
        return adjacencyList.containsKey(v);
    }

    public boolean containsEdge(T u, T v) {
        Code.markCurrentLine(); checkBuilt();
        return adjacencyList.containsKey(u) && adjacencyList.get(u).contains(v);
    }

    public List<T> getNeighbors(T v) {
        Code.markCurrentLine(); checkBuilt();
        return new ArrayList<>(adjacencyList.getOrDefault(v, List.of()));
    }

    // Traversal animations
//    public String bfs(T start) {
//        Code.markCurrentLine(); checkBuilt();
//        return "BFS -> " + animator.runBFSTraversal(adjacencyList, start).toString();
//    }
//
//    public String dfs(T start) {
//        Code.markCurrentLine(); checkBuilt();
//        return "DFS -> " + animator.runDFSTraversal(adjacencyList, start).toString();
//    }

    // Utilities
    public int vertexCount() {
        Code.markCurrentLine(); checkBuilt();
        return adjacencyList.size();
    }

    public int edgeCount() {
        Code.markCurrentLine(); checkBuilt();
        return adjacencyList.values().stream().mapToInt(List::size).sum();
    }

    public void clear() {
        adjacencyList.clear();
    }
}
