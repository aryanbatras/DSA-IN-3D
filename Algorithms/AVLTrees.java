package Algorithms;

import Collections.JAVLTree;

public enum AVLTrees {

    INORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JAVLTree<T> avl) {
            System.out.println(avl.inorder());
        }
    },

    PREORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JAVLTree<T> avl) {
            System.out.println(avl.preorder());
        }
    },

    POSTORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JAVLTree<T> avl) {
            System.out.println(avl.postorder());
        }
    },

    HEIGHT_OF_TREE {
        public <T extends Comparable<T>> void run(JAVLTree<T> avl) {
            System.out.println(avl.getHeight());
        }
    },

    LEAF_NODES {
        public <T extends Comparable<T>> void run(JAVLTree<T> avl) {
            System.out.println(avl.leaves());
        }
    };

    public abstract <T extends Comparable<T>> void run(JAVLTree<T> avl);
}
