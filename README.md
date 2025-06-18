# DSA-IN-3D:  3D Data Structure Visualizer In Java

**Built in 2 Weeks · 100% Pure Java · Zero Dependencies · No Maven/Gradle · Just Clone and Run in IntelliJ**
 
[![DSA-IN-3D: 3D Data Structure Visualizer In Java](https://img.youtube.com/vi/PxCY7eM119E/0.jpg)](https://youtu.be/PxCY7eM119E)

## 🔥 What is DSA-IN-3D?

> DSA-IN-3D is a full-fledged **3D data structure visualizer** built from scratch in Java.
> > It’s made to **educate**, **demonstrate**, and **inspire**—perfect for students, teachers, and curious developers who want to **see algorithms and data structures come alive in 3D**
> 
> > It features ray tracing, realistic rendering, camera animations, interactive and video modes, and an intuitive `.with()` API inspired by the Java Collections framework.

## 💡 Features & Customizations

### 🧱 Supported Data Structures

* `JLinkedList<T>` ✅
* `JArrayList<T>`  ✅
* `JAVLTree<T>`    ✅
* `JStack<T>`      ✅
* `JQueue<T>`      ✅
* `JTrees<T>`      ✅
* `JGraph<T>`      ✅
* `JHeap<T>`       ✅

### 🎞️ Operation Visualizers

* `.get()`, `.set()`, for `JArrayList` and `JLinkedList`
* `.add()`, `.remove()`,  for `JArrayList`, `JLinkedList`, `JHeaps`, `JAVLTree`
* `.push()`, `.pop()`,  for `JStack` and `.offer()` and `.poll()` for `JQueue`
* `.isGreater()`, `.isSmaller()`, `.isGreaterOrEqual()`, `.isSmallerOrEqual()` for `JArrayList`
* `.addVertex()`, `.removeVertex()`, `.addEdge()`, `.removeEdge()`, `.dfs()`, `.bfs()` for `JGraph`
* `.getMin()`, `.getMax()`, `.getHeight()`, `.search()`, `.leaves()`, `.preorder()`, `.postorder()`, `.inorder()` for `JAVLTree` and `JTrees`

---

## 🚧 Our Development Journey

### Phase 1: Basic Setup and Vision

* Minimal ray tracer created, basic `JBox` class drawn with numbers.
* Array-based box line generation: `.add(5)` triggers an animated sliding box.
* Added insertion + deletion animation.
* Implemented video generation using FFmpeg.

### Phase 2: Library-Like Architecture

* Introduced `JArrayListAnimator`, separating rendering, box animation, and camera logic.
* `.withSubtitles()`, `.withInsertAnimation()`, `.withDeleteAnimation()` added.
* Introduced builder-style API like `JArrayList<Integer> list = new JArrayList<>().withXYZ()`

### Phase 3: Rendering Challenges & Optimization

* Added rendering optimizations and GUI progress bars.
* Introduced **live rendering mode**.

### Phase 4: Randomness & Subtitles

* Randomize camera, texture, insert animations.
* Subtitle system added with perfect frame-by-frame sync.
* Modes: `VIDEO`, `LIVE`, `STEP_WISE`, `STEP_WISE_INTERACTIVE`.

### Phase 5–6: Shared Rendering Pipeline

* Encoders optimized with shared FFMPEG stream logic (12 edge cases handled!).
* Quality, naming, reuse of encoder logic finalized.
* Screen resolution scale chaining added.

### Phase 7–8: Data Structures + Algorithms

* `JStacks`, `JQueues`, `JTrees` done. `JGraphs`, `JHeaps`, `JAVLTree` also done.
* Added sorting/searching: `bubble`, `selection`, `insertion`, `binary search`,  `DFS`, `BFS` etc.
* Fully type-generic: `JArrayList<String>`, `JTree<Double>` etc. work out of the box.

---

## ⚙️ Getting Started

### 🔧 Requirements

* Java 17+
* IntelliJ IDEA (recommended)
* No Maven. No Gradle.

### 💻 Run it Now

#### Using Git Clone

```bash
git clone https://github.com/aryanbatras/DSA-IN-3D
```
1. Open the project in IntelliJ.
2. Navigate to `Main.java`
3. Click **Run** — done!

#### Using Just Intellij

1. Open IntelliJ
2. Click on Clone Repository
3. Paste the URL: `https://github.com/aryanbatras/DSA-IN-3D`
4. Click on Clone
5. Open the project in IntelliJ.
6. Navigate to `Main.java`
7. Click **Run** — done!

#### 📦 Using the JAR File in Your Own Project

1. Download the DSA-IN-3D.jar from Releases
2. Place it in your project folder or add it via IntelliJ:
3. File → Project Structure → Libraries → + → Add JAR/Directory 
4.  Create Your Own Main.java
  
```java 

// Main.java

import Collections.*;
import Animations.*;
import Algorithms.*;
import Rendering.*;

public class Main {
public static void main(String[] args) {
    
// Create your structure
JArrayList<Integer> arr = new JArrayList<>();

        // Customize it with infinite possibilities
        arr
            .withRemoveAnimation(Exit.SHRINK_AND_DROP)
            .withInsertAnimation(Entrance.BOUNCE)
            .withBackground(Scenery.GLASS_PASSAGE)
            .withQuality(Resolution.BALANCE)
            .withRenderMode(Render.VIDEO)
            .withOutput("my_array.mp4")
            .build();

        // Perform some operations
        arr.add(10);
        arr.add(20);
        arr.add(30);
        arr.remove(1);
    }
}
```
5.  Compile & Run the Program

> On macOS/Linux:
> ```bash
> javac -cp "DSA-IN-3D.jar" Main.java
> java -cp ".:DSA-IN-3D.jar" Main
> ```
> On Windows:
> ```bash
> javac -cp "DSA-IN-3D.jar" Main.java
> java -cp ".;DSA-IN-3D.jar" Main
> ```
> 
✅ That's it! Your customized 3D animation will be saved as an .mp4 file in your working directory.

---

### 🛠️ Configurable Options

```java


// ✅ Create your data structure
JArrayList<Integer> arr = new JArrayList<>();

// ✅ Perform usual operations
arr.add(10);
arr.add(20);
arr.add(30);
arr.add(40);
arr.add(50);
arr.remove(0);
arr.remove(2);

// ⚠️ Nothing will be rendered unless you configure and call .build()

// 🔀 Randomizer-based configuration
arr
  .withInsertAnimation(Dynamo.randomInsertAnimation())
  .withRemoveAnimation(Dynamo.randomRemoveAnimation())
  .withBackground(Dynamo.randomBackground())
  .withMaterial(Dynamo.randomMaterial())
  .withParticle(Dynamo.randomParticle())
  .withStepsPerAnimation(Frames.NORMAL)
  .withRenderMode(Render.STEP_WISE)
  .withQuality(Resolution.FASTEST)
  .build();

// 🎉 Surprise mode: total chaos!
arr
  .withRandomizer(
  Dynamo.INSTANCE
          .withCrazyMode()
          .withoutRenderMode()
          .withoutQuality()
          .withoutSteps()
   )
    .withStepsPerAnimation(Frames.NORMAL)
    .withQuality(Resolution.BALANCE)
    .withRenderMode(Render.LIVE)
    .build();

// 🎨 Fine-grained custom animation config
arr
  .withBackgroundChangeOnEveryOperation(true)
  .withRemoveAnimation(Exit.SHRINK_AND_DROP)
  .withStepsPerAnimation(Frames.VERY_SLOW)
  .withCameraRotations(View.ROTATE_YAW)
  .withInsertAnimation(Entrance.BOUNCE)
  .withQuality(Resolution.FASTEST)
  .withMaterial(Texture.CHROME)
  .withParticle(Effect.AURORA)
  .withAntiAliasing(Smooth.X2)
  .withCameraFocus(Zoom.X16)
  .withCameraSpeed(Pace.X4)
  .build();

// 🎥 Create your own video
arr
  .withSharedEncoder(true)
  .withRenderMode(Render.VIDEO)
  .withQuality(Resolution.BALANCE)
  .withStepsPerAnimation(Frames.NORMAL)
  .withBackground(Scenery.GLASS_PASSAGE)
  .withOutput("randomizer.mp4")
  .build();

// 🔁 Use inbuilt algorithms

// Option 1: Direct configuration and run
arr
  .withAlgoVisualizer(Array.BUBBLE_SORT)
  .withStepsPerAnimation(Frames.NORMAL)
  .withRenderMode(Render.STEP_WISE)
  .withQuality(Resolution.BALANCE)
  .build()
  .run();

// Option 2: Minimal syntax
Array.BUBBLE_SORT.run(arr);

// Or ✍️ Write your own algorithm visually
for (int i = 0; i < arr.size() - 1; i++) {
    for (int j = 0; j < arr.size() - i - 1; j++) {
        if (arr.isGreater(j, j + 1)) {
            int temp = arr.get(j);
            arr.set(j, j + 1);
            arr.set(j + 1, temp);
        }
    }
}
```

#### 🔗  Chain Video Encoders
You can chain multiple data structures together, run them in same or different modes, and render using custom or shared encoders. Both will use the same encoder stream and render in sequence as you write your code !
```java 
JArrayList<Integer> arr = new JArrayList<>()
.withRenderMode(Render.VIDEO)
.withSharedEncoder(true)
.build();

JStack<Integer> stack = new JStack<>()
.withRenderMode(Render.VIDEO)
.withSharedEncoder(true)
.build();
```

#### 🔗 Infinite Customization & Multi-Structure Scenes

DSA-IN-3D supports not only supports individual data structure animations but also the ability to chain and combine multiple structures together in a single scene. You can create a JArrayList, a JTree, and a JGraph side by side each with its own unique animations, styles, and rendering configurations.

* Every structure can be customized independently using the .with() API
* Run each one in a different render mode ( LIVE, VIDEO, STEP_WISE )
* Use distinct camera angles, textures, backgrounds, and animation speeds

```java
    JAVLTree avl = new JAVLTree()
        .withRandomizer(Dynamo.INSTANCE.withCrazyMode().withoutQuality().withoutRenderMode().withoutSteps())
        .withQuality(Resolution.BEST).withRenderMode(Render.LIVE).build();

    JLinkedList list = new JLinkedList()
        .withRandomizer(Dynamo.INSTANCE.withCrazyMode().withoutQuality().withoutRenderMode().withoutSteps())
        .withQuality(Resolution.BEST).withRenderMode(Render.STEP_WISE).build();
        
    JStack stack = new JStack()
        .withRandomizer(Dynamo.INSTANCE.withCrazyMode().withoutQuality().withoutRenderMode().withoutSteps())
        .withQuality(Resolution.BEST).withRenderMode(Render.STEP_WISE_INTERACTIVE).build();
       
        int value;
        Random rand = new Random();
        for(int i = 0; i < 5; i++) {
            value = rand.nextInt(100);
            stack.push(value);
            list.add(value);
            avl.add(value);
        }        
```

With infinite customization, DSA-IN-3D is the ultimate tool to bring your data structures and algorithms to life in 3D.

---

### 🌈 Modes of Usage

* `LIVE` – live visualization
* `VIDEO` – export to high-quality `.mp4`
* `STEP_WISE` – step-by-step visualization
* `STEP_WISE_INTERACTIVE` – full mouse/keyboard camera

---

## 🌍 Contributions Welcome

* Adding new structures: `JTrie`, `JSkipList`, `JSegmentTree`, etc. [Recommended]
* Improving animation logic or visual effects
* Fixing bugs / improving performance
* Creating new `.with()` APIs

---

## 📊 Big Numbers, Big Effort

* 🧠 3500+ lines of pure Java
* 🎞️ 12+ algorithm visualizations
* 🧱 30+ animator and structure classes
* 🚀 Built completely in **2 weeks** of effort
* 🌄 Built-in ray tracing with realistic HDR 
* 🧰 25+ `.with()` customizable API flags

---

## 🧠 Challenges We Solved

* Custom ray tracer with reflections + soft shadows
* Resolution, naming, quality chaining support
* Shared FFMPEG encoder optimization
* Subtitle rendering with exact timing
* Frame-by-frame animation syncing
* Multi-structure shared scenes
* Interactive camera logic

---


## 📌 License & Credits

**License:** Apache 2.0

**Made by:** Aryan Batra [@aryanbatras](https://github.com/aryanbatras)

**Special Thanks To:** Everyone who joins this mission to make data structures visual, beautiful, and joyful.

---

