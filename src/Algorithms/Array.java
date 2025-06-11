package Algorithms;

import Collections.JArrayList;

public enum Array {
    BUBBLE {
        public void run(JArrayList arr) {
            for (int i = 0; i < arr.size() - 1; i++) {
                for (int j = 0; j < arr.size() - i - 1; j++) {
                    if (arr.get(j) > arr.get(j + 1)) {
                        swap(arr, j, j + 1);
                    }
                }
            }
        }
    },

    INSERTION {
        public void run(JArrayList arr) {
            for (int i = 1; i < arr.size(); i++) {
                int key = arr.get(i);
                int j = i - 1;
                while (j >= 0 && arr.get(j) > key) {
                    arr.set(j + 1, arr.get(j));
                    j--;
                }
                arr.set(j + 1, key);
            }
        }
    },

    SELECTION {
        public void run(JArrayList arr) {
            for (int i = 0; i < arr.size(); i++) {
                int minIdx = i;
                for (int j = i + 1; j < arr.size(); j++) {
                    if (arr.get(j) < arr.get(minIdx)) {
                        minIdx = j;
                    }
                }
                swap(arr, i, minIdx);
            }
        }
    },

    REVERSE {
        public void run(JArrayList arr) {
            int n = arr.size();
            for (int i = 0; i < n / 2; i++) {
                swap(arr, i, n - i - 1);
            }
        }
    },

    SHIFT_LEFT {
        public void run(JArrayList arr) {
            if (arr.size() == 0) return;
            int first = arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                arr.set(i - 1, arr.get(i));
            }
            arr.set(arr.size() - 1, first);
        }
    },

    SHIFT_RIGHT {
        public void run(JArrayList arr) {
            if (arr.size() == 0) return;
            int last = arr.get(arr.size() - 1);
            for (int i = arr.size() - 2; i >= 0; i--) {
                arr.set(i + 1, arr.get(i));
            }
            arr.set(0, last);
        }
    };

    public abstract void run(JArrayList arr);

    protected void swap(JArrayList arr, int i, int j) {
        int tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }
}
