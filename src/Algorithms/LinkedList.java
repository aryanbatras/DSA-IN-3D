package Algorithms;

import Collections.JLinkedList;

public enum LinkedList {

    REVERSE {
        public void run(JLinkedList list) {
            int n = list.size();
            for (int i = 0; i < n / 2; i++) {
                int temp = list.get(i);
                list.set(i, list.get(n - 1 - i));
                list.set(n - 1 - i, temp);
            }
        }
    },

    REMOVE_DUPLICATES {
        public void run(JLinkedList list) {
            for (int i = 0; i < list.size(); i++) {
                int current = list.get(i);
                for (int j = i + 1; j < list.size(); ) {
                    if (list.get(j) == current) {
                        list.remove(j);
                    } else {
                        j++;
                    }
                }
            }
        }
    },

    PAIRWISE_SWAP {
        public void run(JLinkedList list) {
            for (int i = 0; i + 1 < list.size(); i += 2) {
                int temp = list.get(i);
                list.set(i, list.get(i + 1));
                list.set(i + 1, temp);
            }
        }
    },

    KTH_FROM_END {
        public void run(JLinkedList list) {
            int k = 2;
            int n = list.size();
            if (k <= n) {
                int value = list.get(n - k);
                list.clear();
                list.add(value);
            }
        }
    },

    DELETE_MIDDLE {
        public void run(JLinkedList list) {
            int n = list.size();
            if (n == 0) return;
            int mid = n / 2;
            list.remove(mid);
        }
    };

    public abstract void run(JLinkedList list);
}
