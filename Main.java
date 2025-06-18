import Animations.Dynamo;
import Animations.Entrance;
import Animations.Exit;
import Collections.*;
import Algorithms.*;
import Rendering.*;

public class Main {
    public static void main(String[] args) {




































  /*

      JArrayList arr = new JArrayList()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withOutput("stackay.mp4")
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .build();

        arr.add(100);
        arr.add(90);
        arr.add(60);
        arr.add(80);
        arr.add(40);
        arr.add(10);

        arr.remove(0);
        arr.remove(2);

        Array.BUBBLE_SORT.run(arr);


        JStack stack = new JStack()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withOutput("stack.mp4")
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X1)
                .withSharedEncoder(true)
                .build();

        stack.push(100);
        stack.push(90);
        stack.push(60);
        stack.push(80);

        stack.pop();
        stack.pop();

        Stack.REVERSE_STACK.run(stack);

           JQueue queue = new JQueue()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withOutput("queue.mp4")
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X1)
                .withSharedEncoder(true)
                .build();

        queue.offer(100);
        queue.offer(90);
        queue.offer(60);
        queue.offer(80);

        queue.poll();
        queue.poll();

        Queue.ROTATE_ONCE.run(queue);

               JLinkedList list = new JLinkedList()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .withOutput("list.mp4")
                .build();

        list.add(100);
        list.add(90);
        list.add(60);
        list.add(60);
        list.add(80);

        list.remove(0);
        list.remove(0);

   JTrees trees = new JTrees()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .withOutput("trees.mp4")
                .build();

        trees.add(100);
        trees.add(90);
        trees.add(60);
        trees.add(120);
        trees.add(110);
        trees.add(20);

        trees.inorder();
        trees.leaves();

        trees.remove(0);
        trees.remove(0);


        JMinHeap minheap = new JMinHeap()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.FASTEST)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .withOutput("minheap.mp4")
                .build();

        minheap.withRenderMode(Render.VIDEO).build();

        minheap.add(6);
        minheap.add(3);
        minheap.add(2);
        minheap.add(5);
        minheap.add(1);

        minheap.remove();
        minheap.remove();


        JMaxHeap maxheap = new JMaxHeap()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .withOutput("maxheap.mp4")
                .build();

        maxheap.withRenderMode(Render.VIDEO).build();

        maxheap.add(6);
        maxheap.add(3);
        maxheap.add(2);
        maxheap.add(5);
        maxheap.add(1);

        MaxHeap.HEAP_SORT.run(maxheap);

        JGraph graph = new JGraph()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X2)
                .withSharedEncoder(true)
                .withOutput("graphDFS.mp4")
                .build();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addEdge(1, 2);
        graph.addEdge(2, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(1, 5);

        graph.withRenderMode(Render.VIDEO).build();
        Graph.DEPTH_FIRST_TRAVERSAL.run(graph);


   JAVLTrees avl = new JAVLTrees()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withParticle(Effect.GRADIENT)
                .withQuality(Resolution.BEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .withOutput("trees.mp4")
                .build();

        avl.add(100);
        avl.add(90);
        avl.add(60);
        avl.add(120);
        avl.add(110);
        avl.add(20);

        avl.remove(0);
        avl.remove(0);

   */
//
//       JAVLTree avl = new JAVLTree()
//               .withInsertAnimation(Entrance.BOUNCE)
//               .withRemoveAnimation(Exit.SHRINK_AND_DROP)
//               .withBackgroundChangeOnEveryOperation(true)
//               .withRandomizer(Dynamo.INSTANCE)
//               .withParticle(Effect.GRADIENT)
//               .withMaterial(Texture.METAL)
//               .withRenderMode(Render.LIVE)
//               .withQuality(Resolution.BALANCE)
//               .withStepsPerAnimation(Frames.NORMAL)
//               .withAlgoVisualizer(AVLTrees.INORDER_TRAVERSAL)
//               .build();
//
//       avl.add(100);
//       avl.add(50);
//        avl.add(25);
//


    JArrayList<Integer> arr = new JArrayList<>().build();

    arr.add(10);
    arr.add(20);
    arr.add(30);
    arr.add(40);
    arr.add(50);

    // ANIMATIONS WITH INFINITE CUSTOMIZATIONS
        arr
                .withInsertAnimation(Entrance.BOUNCE)
                .withRemoveAnimation(Exit.SHRINK_AND_DROP)
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_FAST)
                .withQuality(Resolution.FASTEST)
                .withMaterial(Texture.CHROME)
                .withParticle(Effect.AURORA)
                .build();

        // CREATE OUR OWN VIDEOS
        arr
                .withRenderMode(Render.VIDEO)
                .withStepsPerAnimation(Frames.NORMAL)
                .withQuality(Resolution.BALANCE)
                .build();


    // BUBBLE SORT
        for(int i = 0; i < arr.size() - 1; i++){
            for(int j = 0; j < arr.size() - i - 1; j++){
                if(arr.isGreater(j, j + 1)){
                    // swap
                    int temp = arr.get(j);
                    arr.set(j, j + 1);
                    arr.set(j + 1, temp);
                }
            }
        }














































//        JArrayList<Integer> arr = new JArrayList<>().build();
//
//        // INSERTING ELEMENTS
//        int x = 5; int y = 10;
//        for(int k = 3; k > 0; k--){
//            arr.add(x * y);
//            arr.add(x + y);
//        }
//
//        // RANDOM ANIMATIONS
//        arr
//                .withInsertAnimation(Dynamo.randomInsertAnimation())
//                .withRemoveAnimation(Dynamo.randomRemoveAnimation())
//                .withRandomizer(Dynamo.INSTANCE.withCrazyMode())
//                .build();
//
//        // INFINITE CUSTOMIZATIONS
//        arr
//                .withMaterial(Texture.CHROME)
//                .withParticle(Effect.GRADIENT)
//                .withInsertAnimation(Entrance.BOUNCE)
//                .withRemoveAnimation(Exit.SHRINK_AND_DROP)
//                .withBackgroundChangeOnEveryOperation(true)
//                .withQuality(Resolution.BALANCE)
//                .withRenderMode(Render.LIVE)
//                .build();
//
//
//        // BUBBLE SORT
//
//        for(int i = 0; i < arr.size() - 1; i++){
//            for(int j = 0; j < arr.size() - i - 1; j++){
//                if(arr.isGreater(j, j + 1)){
//                    // swap
//                    int temp = arr.get(j);
//                    arr.set(j, j + 1);
//                    arr.set(j + 1, temp);
//
//                }
//            }
//        }
//
//        // RUNNING IN BUILT BINARY SEARCH AFTER SORTING
//        Array.BINARY_SEARCH.run(arr);































//        for (int i = 0; i < arr.size() - 1; i++) {
//            for (int j = 0; j < arr.size() - i - 1; j++) {
//                if (arr.isGreater(j, j + 1)) {
//                    swap(arr, j, j + 1);
//                }
//            }
//        }

        JArrayList arr2 = new JArrayList()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_FAST)
                .withParticle(Effect.GRADIENT)
                .withOutput("mergeSort.mp4")
                .withQuality(Resolution.FASTEST)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .withSharedEncoder(true)
                .build();

        arr2.add(100);
        arr2.add(20);
        arr2.add(50);
        arr2.add(80);
        arr2.add(30);

        arr2.withRenderMode(Render.VIDEO).withQuality(Resolution.BEST).withStepsPerAnimation(Frames.VERY_SLOW).build();
        Array.MERGE_SORT.run(arr2);
    }
}










