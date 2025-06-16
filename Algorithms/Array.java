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
    },


    MERGE_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            mergeSort(arr, 0, arr.size() - 1);
        }
    },

    COUNTING_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0 || !(arr.get(0) instanceof Integer)) return;
            arr.checkInteger();

            int max = (Integer) arr.get(0);
            int min = (Integer) arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                int val = (Integer) arr.get(i);
                if (val > max) max = val;
                if (val < min) min = val;
            }

            int range = max - min + 1;
            int[] count = new int[range];
            for (int i = 0; i < arr.size(); i++) {
                count[(Integer) arr.get(i) - min]++;
            }

            int index = 0;
            for (int i = 0; i < range; i++) {
                while (count[i]-- > 0) {
                    arr.set(index++, (T)(Integer)(i + min));
                }
            }
        }
    },

    RADIX_SORT {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0 || !(arr.get(0) instanceof Integer)) return;
            arr.checkInteger();

            int max = (Integer) arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                int val = (Integer) arr.get(i);
                if (val > max) max = val;
            }

            for (int exp = 1; max / exp > 0; exp *= 10) {
                countingSortByDigit(arr, exp);
            }
        }
    },

    LINEAR_SEARCH {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0) return;
            T target = arr.getRandomElement();

            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).compareTo(target) == 0) {
                    System.out.println( "Found: " + target );
                    return;
                }
            }
            System.out.println(("Not Found: " + target) );
        }
    },

    BINARY_SEARCH {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            boolean ascending = arr.checkSorted();
            T target = arr.getRandomElement();
            System.out.println( "Target: " + target );
            int low = 0, high = arr.size() - 1;

            while (low <= high) {
                int mid = (low + high) / 2;
                int cmp = target.compareTo(arr.get(mid));
                if (cmp == 0) {
                    System.out.println( "Found: " + target );
                    return;
                }

                if ((ascending && cmp < 0) || (!ascending && cmp > 0)) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            System.out.println(("Not Found: " + target) );
        }
    },

    INTERPOLATION_SEARCH {
        public <T extends Comparable<T>> void run(JArrayList<T> arr) {
            if (arr.size() == 0 || !(arr.get(0) instanceof Integer)) return;
            arr.checkSorted();
            arr.checkInteger();

            int target = (Integer) arr.getRandomElement();

            int low = 0, high = arr.size() - 1;

            while (low <= high && target >= (Integer) arr.get(low) && target <= (Integer) arr.get(high)) {
                int valLow = (Integer) arr.get(low);
                int valHigh = (Integer) arr.get(high);
                if (valLow == valHigh) break;

                int pos = low + ((target - valLow) * (high - low)) / (valHigh - valLow);

                if ((Integer) arr.get(pos) == target) {
                    System.out.println( "Found: " + target );
                    return;
                }

                if ((Integer) arr.get(pos) < target) low = pos + 1;
                else high = pos - 1;
            }
            System.out.println(("Not Found: " + target) );
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

    private static <T extends Comparable<T>> void mergeSort(JArrayList<T> arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static <T extends Comparable<T>> void merge(JArrayList<T> arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Object[] L = new Object[n1];
        Object[] R = new Object[n2];

        for (int i = 0; i < n1; i++) L[i] = arr.get(left + i);
        for (int i = 0; i < n2; i++) R[i] = arr.get(mid + 1 + i);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (((T) L[i]).compareTo((T) R[j]) <= 0) {
                arr.set(k++, (T) L[i++]);
            } else {
                arr.set(k++, (T) R[j++]);
            }
        }

        while (i < n1) arr.set(k++, (T) L[i++]);
        while (j < n2) arr.set(k++, (T) R[j++]);
    }

    private static <T extends Comparable<T>> void countingSortByDigit(JArrayList<T> arr, int exp) {
        int n = arr.size();
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++) {
            int val = (Integer) arr.get(i);
            count[(val / exp) % 10]++;
        }

        for (int i = 1; i < 10; i++) count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int val = (Integer) arr.get(i);
            output[count[(val / exp) % 10] - 1] = val;
            count[(val / exp) % 10]--;
        }

        for (int i = 0; i < n; i++) {
            arr.set(i, (T)(Integer) output[i]);
        }
    }

}

