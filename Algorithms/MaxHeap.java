package Algorithms;

import Collections.JMaxHeap;

public enum MaxHeap {

    INORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMaxHeap<T> heap) {
        }
    },

    PREORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMaxHeap<T> heap) {
        }
    },

    POSTORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMaxHeap<T> heap) {
        }
    },

    HEIGHT_OF_TREE {
        public <T extends Comparable<T>> void run(JMaxHeap<T> heap) {
        }
    },

    LEAF_NODES {
        public <T extends Comparable<T>> void run(JMaxHeap<T> heap) {
        }
    };

    public abstract <T extends Comparable<T>> void run(JMaxHeap<T> heap);
}
