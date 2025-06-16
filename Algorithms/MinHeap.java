package Algorithms;

import Collections.JMinHeap;

public enum MinHeap {

    FIND_K_SMALLEST {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
            int k = Math.min(5, heap.size());
            System.out.println(("Finding " + k + " smallest elements using MinHeap") );

            for (int i = 0; i < k; i++) {
                T min = heap.remove();
                System.out.println( "Extracted: " + min );
            }

            System.out.println(("Extracted top " + k + " smallest elements") );
        }
    },

    HEAP_SORT {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
            System.out.println(("Heap Sort: Removing all elements in sorted order") );

            int size = heap.size();
            for (int i = 0; i < size; i++) {
                T min = heap.remove();
                System.out.println("Sorted: " + min );
            }

            System.out.println(("Heap Sort complete!") );
        }
    },

    PEEK_TOP {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
            if (heap.isEmpty()) {
                System.out.println(("Heap is empty!") );
                return;
            }

            T top = heap.getPriority();
            System.out.println(("Top element (Min): " + top) );
        }
    };

    public abstract <T extends Comparable<T>> void run(JMinHeap<T> heap);
}
