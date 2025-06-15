package Algorithms;

import Collections.JTrees;
import java.util.*;

public enum Trees {

    INORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JTrees<T> tree) {
            System.out.println(tree.inorder());
        }
    },

    PREORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JTrees<T> tree) {
            System.out.println(tree.preorder());
        }
    },

    POSTORDER_TRAVERSAL {
        public <T extends Comparable<T>> void run(JTrees<T> tree) {
            System.out.println(tree.postorder());
        }
    },

    HEIGHT_OF_TREE {
        public <T extends Comparable<T>> void run(JTrees<T> tree) {
            int height = tree.getHeight();
            System.out.println("Height of tree: " + height);
        }
    },

    LEAF_NODES {
        public <T extends Comparable<T>> void run(JTrees<T> tree) {
            System.out.println(tree.leaves());
        }
    };

    public abstract <T extends Comparable<T>> void run(JTrees<T> tree);
}
