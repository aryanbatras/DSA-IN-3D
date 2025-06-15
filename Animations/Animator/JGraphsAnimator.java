package Animations.Animator;

import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;
import Animations.*;
import Collections.JGraphs;
import Shapes.Core.Shape;
import Shapes.JBox;
import Rendering.*;
import java.util.*;
import java.util.stream.Collectors;

import Utility.*;

public class JGraphsAnimator<T> {
    private final ArrayList<Shape> world;
    private final Utility.Camera camera;
    private final Renderer renderer;
    private Subtitle subtitle;

    private int framesPerSecond;
    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Render mode;
    private double scale;
    private Texture material;
    private String background;
    private Effect particle;
    private boolean randomBackground;
    private Random rand;

    private final Map<T, JBox> valueToBox = new LinkedHashMap<>();
    private final ArrayList<JBox> edgeBars = new ArrayList<>();

    public JGraphsAnimator() {
        this.world = new ArrayList<>();
        this.camera = new Utility.Camera();
        this.framesPerSecond = 20;
        this.renderer = new Renderer("Resources/lake.jpg");
        this.background = "Resources/lake.jpg";
        this.subtitle = new Subtitle("Graph");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.mode = Render.DISABLED;
        this.scale = 0.5;
        this.material = Texture.METAL;
        this.particle = Effect.NONE;
        this.randomBackground = false;
        this.rand = new Random();
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

    public void setBackground(String bg) {
        this.background = bg;
        renderer.setBackground(bg);
    }

    public void setMaterial(Texture mat) {
        this.material = mat;
    }

    public void setParticle(Effect part) {
        this.particle = part;
    }

    private void setRandomBackground() {
        Scenery rnd = Scenery.values()[new Random().nextInt(Scenery.values().length)];
        setBackground(rnd.toString());
    }

    public void setRandomizeBackgroundAsTrue() {
        this.randomBackground = true;
    }

    public void setCameraRotation(View v) {
        cameraAnimator.setCameraRotation(v);
        boxAnimator.setCameraRotation(cameraAnimator, v);
    }

    public void setCameraSpeed(double speed) {
        cameraAnimator.setSpeed(speed);
        boxAnimator.setSpeed(speed);
    }

    public void setAntiAliasing(double antiAliasing) {
        renderer.setAntialiasing(antiAliasing);
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius( ) - focus);
    }

    public void runAddVertexAnimation(T value, Entrance anim) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Add Vertex");
        subtitle.setValue(String.valueOf(value));

        JBox newBox = new JBox(
                new Point(0, 0, 0),
                1.0, 1.0, 1.0,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, String.valueOf(value), particle
        );

        world.add(newBox);
        valueToBox.put(value, newBox);

        int count = valueToBox.size();
        int exp = 31 - Integer.numberOfLeadingZeros(count);
        double radius = 10 + 1.5 * exp;

        int index = 0;
        Point target = null;
        for (Map.Entry<T, JBox> entry : valueToBox.entrySet()) {
            JBox box = entry.getValue();
            double angle = 2 * Math.PI * index / count;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            double z = 0;

            target = new Point(x, y, z);
            box.center = target;
            index++;
        }

        cameraAnimator.slideTo(target);
        boxAnimator.shakeSlow(newBox);

        removeAllEdges();
        edgeDrawCount = new HashMap<>();
        for (Map.Entry<T, T> entry : edgeMap.entrySet()) {
            T u = entry.getKey();
            T v = entry.getValue();

            JBox from = valueToBox.get(u);
            JBox to   = valueToBox.get(v);
            if (from == null || to == null) continue;

            Point pu = from.getCenter();
            Point pv = to.getCenter();

            int drawIndex = nextDrawIndex(u, v);

            List<JBox> bars = makeBar(pu, pv, 0.1, u + "->" + v, drawIndex);

            for (JBox bar : bars) {
                world.add(bar);
                edgeBars.add(bar);
            }
        }

    }

    private void removeAllEdges() {
        for (JBox bar : edgeBars) {
            world.remove(bar);
        }
        edgeBars.clear();
    }

    public void runRemoveVertexAnimation(T value, Exit anim) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Remove Vertex");
        subtitle.setValue(value.toString());

        JBox node = valueToBox.get(value);
        if (node == null) return;

        List<T> keysToRemove = new ArrayList<>();
        List<T> reverseKeysToRemove = new ArrayList<>();

        Iterator<Map.Entry<T, T>> iterator = edgeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<T, T> entry = iterator.next();
            T u = entry.getKey();
            T v = entry.getValue();

            if (u.equals(value) || v.equals(value)) {
                iterator.remove();
                runRemoveEdgeAnimation(u, v, Exit.FADE_UP);
                runRemoveEdgeAnimation(v, u, Exit.FADE_UP);
            }
        }


        cameraAnimator.slideTo(node.getCenter());
        boxAnimator.highlight(node);
        boxAnimator.scaleDown(node);

        world.remove(node);
        valueToBox.remove(value);
    }

    private final Map<T, T> edgeMap = new HashMap<>();

    public void runAddEdgeAnimation(T u, T v, Entrance anim, boolean reverseExists) {

        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Add Edge");
        subtitle.setValue(u + "->" + v);

        JBox boxU = valueToBox.get(u);
        JBox boxV = valueToBox.get(v);
        if (boxU == null || boxV == null) return;

        Point pu = boxU.getCenter();
        Point pv = boxV.getCenter();

        edgeMap.put(u, v);
        System.out.println("Edge Map Updated: " + u + " -> " + v);

        for (var e : valueToBox.entrySet()) {
            Point c = e.getValue().getCenter();
        }

        double thickness = 0.1;

        int drawIndex = nextDrawIndex(u, v);

        List<JBox> bars;
        if(!reverseExists){
            bars = makeBar(pu, pv, thickness, u + "->" + v, drawIndex);
        } else {
           bars = makeBarOffset(pu, pv, thickness, u + "->" + v, drawIndex);
        }


        cameraAnimator.slideTo(pu);
        boxAnimator.highlight(boxU);

        cameraAnimator.slideTo(pv);
        boxAnimator.highlight(boxV);

        for (JBox bar : bars) {
            cameraAnimator.slideTo(bar.getCenter());
            world.add(bar); edgeBars.add(bar);
            boxAnimator.bounceIn(bar, bar.getCenter().y);
            boxAnimator.highlight(bar);
        }

    }

    private Map<String, Integer> edgeDrawCount = new HashMap<>();

    private int nextDrawIndex(T u, T v) {
        String key = u + "->" + v;
        int count = edgeDrawCount.getOrDefault(key, 0);
        edgeDrawCount.put(key, count + 1);
        return count;
    }

    public void runRemoveEdgeAnimation(T u, T v, Exit anim) {
        if (randomBackground) setRandomBackground();
        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        subtitle.setMode("Remove Edge");
        subtitle.setValue(u + "->" + v);

        Iterator<JBox> it = edgeBars.iterator();
        while (it.hasNext()) {
            JBox bar = it.next();
            if (bar.val.equals(u + "->" + v)) {

                JBox boxU = valueToBox.get(u);
                JBox boxV = valueToBox.get(v);
                if (boxU == null || boxV == null) return;

                Point pu = boxU.getCenter();
                Point pv = boxV.getCenter();

                cameraAnimator.slideTo(pu);
                boxAnimator.highlight(boxU);

                cameraAnimator.slideTo(pv);
                boxAnimator.highlight(boxV);

                cameraAnimator.slideTo(bar.getCenter());
                boxAnimator.highlight(bar);
                boxAnimator.scaleDown(bar);

                world.remove(bar);
                it.remove();
            }
        }

        edgeMap.remove(u, v);
        edgeDrawCount.remove(u + "->" + v);
    }


    private List<JBox> makeBar(Point from, Point to, double thickness,
                               String name, int drawIndex) {
        List<JBox> segs = new ArrayList<>();
        double baseZ = -0.25;
        double yOff = (drawIndex) * thickness * 3;
        double xOff = (drawIndex) * thickness * 3;

        Point f = new Point(from.x + xOff + 0.25, from.y + yOff + 0.25, from.z);
        Point t = new Point(to.x + xOff + 0.25,   to.y   + yOff + 0.25, to.z);

        double z = baseZ + 0;

        double dx = t.x - f.x;
        double dy = t.y - f.y;

        boolean needH = Math.abs(dx) > 1e-6;
        boolean needV = Math.abs(dy) > 1e-6;

        if (!needH ^ !needV) {
            if (needH) {
                double w = Math.abs(dx), cx = (f.x + t.x)/2;
                segs.add(new JBox(new Point(cx, f.y, z), w, thickness, thickness,
                        new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));
            } else {
                double h = Math.abs(dy), cy = (f.y + t.y)/2;
                segs.add(new JBox(new Point(f.x, cy, z), thickness, h, thickness,
                        new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));
            }
            return segs;
        }


        boolean horizontalFirst = f.y < t.y;

        if (horizontalFirst) {
            double cx = (f.x + t.x)/2, w = Math.abs(dx);
            segs.add(new JBox(new Point(cx, f.y, z), w, thickness, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));
            double cy = (f.y + t.y)/2, h = Math.abs(dy);
            segs.add(new JBox(new Point(t.x, cy, z), thickness, h, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));

        } else {
            double cy = (f.y + t.y)/2, h = Math.abs(dy);
            segs.add(new JBox(new Point(f.x, cy, z), thickness, h, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));
            double cx = (f.x + t.x)/2, w = Math.abs(dx);
            segs.add(new JBox(new Point(cx, t.y, z), w, thickness, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.WATER));
        }

        return segs;
    }

    private List<JBox> makeBarOffset(
            Point from,
            Point to,
            double thickness,
            String name,
            int drawIndex
    ) {
        List<JBox> segs = new ArrayList<>();
        double baseZ = 0.25 + 0;

        double shift = drawIndex * thickness * 16;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        boolean needH = Math.abs(dx) > 1e-6;
        boolean needV = Math.abs(dy) > 1e-6;

        boolean horizontalFirst = needH && needV && (Math.abs(dx) >= Math.abs(dy));

        double offsetX = 0, offsetY = 0;
        if (!needV && needH) {
            offsetY = shift;
        } else if (!needH && needV) {
            offsetX = shift;
        } else if (horizontalFirst) {
            offsetY = shift;
        } else {
            offsetX = shift;
        }

        Point f = new Point(from.x + offsetX - 0.25, from.y + offsetY - 0.25, from.z);
        Point t = new Point(  to.x + offsetX - 0.25,   to.y + offsetY - 0.25,   to.z);

        double z = baseZ;

        if (needH ^ needV) {
            if (needH) {
                double w  = Math.abs(dx), cx = (f.x + t.x)/2;
                segs.add(new JBox(
                        new Point(cx, f.y, z),
                        w, thickness, thickness,
                        new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
                ));
            } else {
                double h  = Math.abs(dy), cy = (f.y + t.y)/2;
                segs.add(new JBox(
                        new Point(f.x, cy, z),
                        thickness, h, thickness,
                        new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
                ));
            }
            return segs;
        }

        if (horizontalFirst) {
            double w  = Math.abs(dx), cx = (f.x + t.x)/2;
            segs.add(new JBox(
                    new Point(cx, f.y, z),
                    w, thickness, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
            ));
            double h  = Math.abs(dy), cy = (f.y + t.y)/2;
            segs.add(new JBox(
                    new Point(t.x, cy, z),
                    thickness, h, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
            ));
        } else {
            double h  = Math.abs(dy), cy = (f.y + t.y)/2;
            segs.add(new JBox(
                    new Point(f.x, cy, z),
                    thickness, h, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
            ));
            double w  = Math.abs(dx), cx = (f.x + t.x)/2;
            segs.add(new JBox(
                    new Point(cx, t.y, z),
                    w, thickness, thickness,
                    new Color(1f,0.82f,0f), Texture.METAL, 0, name, Effect.NONE
            ));
        }

        return segs;
    }




}
