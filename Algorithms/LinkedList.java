package Algorithms;

import Collections.JLinkedList;

public enum LinkedList {

    REVERSE {
        public <T extends Comparable<T>> void run(JLinkedList<T> list) {
            int n = list.size();
            for (int i = 0; i < n / 2; i++) {
                T temp = list.get(i);
                list.set(i, list.get(n - 1 - i));
                list.set(n - 1 - i, temp);
            }
        }
    },

    REMOVE_DUPLICATES {
        public <T extends Comparable<T>> void run(JLinkedList<T> list) {
            for (int i = 0; i < list.size(); i++) {
                T current = list.get(i);
                for (int j = i + 1; j < list.size(); ) {
                    if (list.get(j).compareTo(current) == 0) {
                        list.remove(j);
                    } else {
                        j++;
                    }
                }
            }
        }
    },

    PAIRWISE_SWAP {
        public <T extends Comparable<T>> void run(JLinkedList<T> list) {
            for (int i = 0; i + 1 < list.size(); i += 2) {
                T temp = list.get(i);
                list.set(i, list.get(i + 1));
                list.set(i + 1, temp);
            }
        }
    },

    KTH_FROM_END {
        public <T extends Comparable<T>> void run(JLinkedList<T> list) {
            int k = 2;
            int n = list.size();
            if (k <= n) {
                T value = list.get(n - k);
                for (int i = list.size() - 1; i >= 0; i--) {
                    list.remove(i);
                }
                list.add(value);
            }
        }
    },

    DELETE_MIDDLE {
        public <T extends Comparable<T>> void run(JLinkedList<T> list) {
            int n = list.size();
            if (n == 0) return;
            int mid = n / 2;
            list.remove(mid);
        }
    };

    public abstract <T extends Comparable<T>> void run(JLinkedList<T> list);
}
