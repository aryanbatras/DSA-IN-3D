package Collections;

import Algorithms.Graph;
import Animations.Animator.JGraphsAnimator;
import Animations.*;
import Rendering.*;
import java.util.*;
import Utility.*;

public class JGraph<T extends Comparable<T>> {

    private final Map<T, List<T>> adjacencyList;
    private final JGraphsAnimator<T> animator;

    // Configuration fields
    private double scale;
    private Encoder encoder;
    private Dynamo randomizer;
    private boolean built;
    private Set<String> explicitlySetProperties;

    // Default animations
    private Entrance defaultVertexEntrance;
    private Exit defaultVertexExit;
    private Entrance defaultEdgeEntrance;
    private Exit defaultEdgeExit;

    // Render mode, output, etc
    private Graph algo;
    private Render mode;
    private String userOutput;
    private boolean userProvidedOutput;
    private boolean preferSharedEncoder;

    public JGraph() {
        this.adjacencyList = new LinkedHashMap<>();
        this.animator = new JGraphsAnimator<>();
        this.scale = 0.25;
        this.mode = Render.DISABLED;
        this.encoder = null;
        this.randomizer = null;
        this.built = true;
        this.explicitlySetProperties = new HashSet<>();
        this.defaultVertexEntrance = Entrance.BOUNCE;
        this.defaultVertexExit = Exit.SCALE_DOWN;
        this.defaultEdgeEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultEdgeExit = Exit.SLIDE_UP;
        animator.setScale(0.25);
        animator.setFPS(1);
    }
    public JGraph withAlgoVisualizer(Graph algo){
        this.algo = algo;
        /*
         you have to build functions to
         sort the internal array as per algo
         because what algo handles is external array
         */
        this.built = false;
        return this;
    }

    public JGraph(JGraph g) {
        this.adjacencyList = g.adjacencyList;
        this.animator = g.animator;
        this.scale = g.scale;
        this.encoder = g.encoder;
        this.randomizer = g.randomizer;
        this.built = g.built;
        this.explicitlySetProperties.addAll(g.explicitlySetProperties);
        this.defaultVertexEntrance = g.defaultVertexEntrance;
        this.defaultVertexExit = g.defaultVertexExit;
        this.defaultEdgeEntrance = g.defaultEdgeEntrance;
        this.defaultEdgeExit = g.defaultEdgeExit;
        this.algo = g.algo;
        this.mode = g.mode;
        this.userOutput = g.userOutput;
        this.userProvidedOutput = g.userProvidedOutput;
        this.preferSharedEncoder = g.preferSharedEncoder;
    }

    public void run() {
        if (algo == null) { throw new IllegalStateException(" No algo was given via .withAlgoVisualizer() "); }
        if (adjacencyList.isEmpty()) { throw new IllegalStateException(" Graph is empty "); }
        algo.run(this);
    }

    public JGraph withInsertAnimation(Entrance entrance) {
        this.defaultVertexEntrance = entrance;
        this.defaultEdgeEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JGraph withRemoveAnimation(Exit exit) {
        this.defaultEdgeExit = exit;
        this.defaultVertexExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JGraph withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JGraph withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JGraph withQuality(Resolution quality) {
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

    public JGraph withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JGraph withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JGraph withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JGraph withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JGraph withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JGraph withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JGraph withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JGraph withAntiAliasing(Smooth antiAliasing) {
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

    public JGraph withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JGraph withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JGraph withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JGraph build() {

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

    public void addVertex(T v) {
        Code.markCurrentLine();
        checkBuilt();
        adjacencyList.putIfAbsent(v, new ArrayList<>());
            animator.runAddVertexAnimation(v,
                    randomizer != null ? randomizer.randomInsertAnimation() : defaultVertexEntrance);

    }

    public T removeVertex(T v) {
        Code.markCurrentLine();
        checkBuilt();
            animator.runRemoveVertexAnimation(v,
                    randomizer != null ? randomizer.randomRemoveAnimation() : defaultVertexExit);

        adjacencyList.remove(v);
        // also remove edges
        for (List<T> nbrs : adjacencyList.values()) nbrs.remove(v);
        return v;
    }

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
            animator.runAddVertexAnimation(u, defaultVertexEntrance);
        }
        if (!adjacencyList.containsKey(v)) {
            adjacencyList.put(v, new ArrayList<>());
             animator.runAddVertexAnimation(v, defaultVertexEntrance);
        }

        boolean reverseExists = adjacencyList.containsKey(v) && adjacencyList.get(v).contains(u);
        adjacencyList.get(u).add(v);

            animator.runAddEdgeAnimation(u, v, randomizer != null ? randomizer.randomInsertAnimation() : defaultEdgeEntrance,
                    reverseExists);

    }


    public void removeEdge(T u, T v) {
        Code.markCurrentLine();
        checkBuilt();
            animator.runRemoveEdgeAnimation(u, v,
                    randomizer != null ? randomizer.randomRemoveAnimation() : defaultEdgeExit);

        List<T> nbrs = adjacencyList.get(u);
        if (nbrs != null) nbrs.remove(v);
    }

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

    public void highlightVertex(T value) {
        Code.markCurrentLine(); checkBuilt();
            animator.runHighlightVertexAnimation(value);

    }

    public void highlightEdge(T u, T v) {
            animator.runHighlightEdgeAnimation(u, v);

    }


    public Map<T, List<T>> getAdjacencyList() {
        return new LinkedHashMap<>(adjacencyList);
    }

    public T[] getVertices() {
        return adjacencyList.keySet().toArray((T[]) new Comparable[0]);
    }

    public void dfs(){
        if (adjacencyList.isEmpty()) { throw new IllegalStateException(" Graph is empty "); }
        Graph.DEPTH_FIRST_TRAVERSAL.run(this);
    }

    public void bfs(){
        if (adjacencyList.isEmpty()) { throw new IllegalStateException(" Graph is empty "); }
        Graph.BREADTH_FIRST_TRAVERSAL.run(this);
    }

}
