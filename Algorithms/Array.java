package Algorithms;

import Collections.JArrayList;

public enum Array {
    BUBBLE_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            for (int i = 0; i < arr.size() - 1; i++) {
                for (int j = 0; j < arr.size() - i - 1; j++) {
                    if (arr.isGreater(j, j + 1)) {
                        swap(arr, j, j + 1);
                    }
                }
            }
        }
    },

    INSERTION_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            for (int i = 1; i < arr.size(); i++) {
                T key = arr.get(i);
                int j = i - 1;
                while (j >= 0 && arr.isGreater(j, i)) {
                    arr.set(j + 1, arr.get(j));
                    j--;
                }
                arr.set(j + 1, key);
            }
        }
    },

    SELECTION_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            for (int i = 0; i < arr.size() - 1; i++) {
                int min = i;
                for (int j = i + 1; j < arr.size(); j++) {
                    if (arr.isSmaller(j, min)) {
                        min = j;
                    }
                }
                swap(arr, i, min);
            }
        }
    },

    REVERSE {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            int n = arr.size();
            for (int i = 0; i < n/2; i++) {
                swap(arr, i, n - 1 - i);
            }
        }
    },

    SHIFT_LEFT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0) return;
            T first = arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                arr.set(i - 1, arr.get(i));
            }
            arr.set(arr.size() - 1, first);
        }
    },

    SHIFT_RIGHT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0) return;
            T last = arr.get(arr.size() - 1);
            for (int i = arr.size() - 2; i >= 0; i--) {
                arr.set(i + 1, arr.get(i));
            }
            arr.set(0, last);
        }
    },

    SHELL_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            for (int gap = arr.size() / 2; gap > 0; gap /= 2) {
                for (int i = gap; i < arr.size(); i++) {
                    T temp = arr.get(i);
                    int j = i;
                    while (j >= gap && arr.isGreater(j - gap, i)) {
                        arr.set(j, arr.get(j - gap));
                        j -= gap;
                    }
                    arr.set(j, temp);
                }
            }
        }
    },

    QUICK_SORT {
        public  <T extends Comparable<T>> void run(JArrayList<T> arr) {
            sort(arr, 0, arr.size()-1);
        }
    },

    HEAP_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            int n = arr.size();
            for (int i = n/2 - 1; i >= 0; i--) heapify(arr, n, i);
            for (int i = n - 1; i > 0; i--) {
                swap(arr, 0, i);
                heapify(arr, i, 0);
            }
        }
    };

    public abstract <T extends Comparable<T>> void run(JArrayList<T> arr);

   private static  <T extends Comparable<T>> void swap(JArrayList<T> arr, int i, int j) {
        T tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }

    private static <T extends Comparable<T>> void sort(JArrayList<T> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(JArrayList<T> arr, int low, int high) {
        T pivot = arr.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr.isSmallerOrEqualTo(j, high)) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static <T extends Comparable<T>> void heapify(JArrayList<T> arr, int size, int root) {
        int largest = root;
        int left = 2*root + 1;
        int right = 2*root + 2;
        if (left < size && arr.isGreater(left, largest)) largest = left;
        if (right < size && arr.isGreater(right, largest)) largest = right;
        if (largest != root) {
            swap(arr, root, largest);
            heapify(arr, size, largest);
        }
    }
}
