package Algorithms;

import Collections.JMinHeap;

public enum MinHeap {

    INORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
        }
    },

    PREORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
        }
    },

    POSTORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
        }
    },

    HEIGHT_OF_TREE {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
        }
    },

    LEAF_NODES {
        public <T extends Comparable<T>> void run(JMinHeap<T> heap) {
        }
    };

    public abstract <T extends Comparable<T>> void run(JMinHeap<T> heap);
}
