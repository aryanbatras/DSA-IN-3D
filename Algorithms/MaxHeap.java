package Algorithms;

import Collections.JMaxHeap;

public enum MaxHeap {

    FIND_K_LARGEST {
        public <T extends Comparable<T>> void run(JMaxHeap<T> maxheap) {
            int k = Math.min(5, maxheap.size());
            System.out.println(("Finding " + k + " largest elements using MinHeap") );

            for (int i = 0; i < k; i++) {
                T max = maxheap.remove();
                System.out.println( "Extracted: " + max );
            }

            System.out.println(("Extracted top " + k + " largest elements") );
        }
    },

    HEAP_SORT {
        public <T extends Comparable<T>> void run(JMaxHeap<T> maxheap) {
            System.out.println(("Heap Sort: Removing all elements in sorted order") );

            int size = maxheap.size();
            for (int i = 0; i < size; i++) {
                T max = maxheap.remove();
                System.out.println("Sorted: " + max );
            }

            System.out.println(("Heap Sort complete!") );
        }
    },

    PEEK_TOP {
        public <T extends Comparable<T>> void run(JMaxHeap<T> maxheap) {
            if (maxheap.isEmpty()) {
                System.out.println(("Heap is empty!") );
                return;
            }

            T top = maxheap.getPriority();
            System.out.println(("Top element (Min): " + top) );
        }
    };


    public abstract <T extends Comparable<T>> void run(JMaxHeap<T> maxheap);
}
