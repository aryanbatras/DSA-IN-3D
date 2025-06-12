package Algorithms;

import Collections.JQueue;

public enum Queue {

    REVERSE_ENTIRE {
        public <T extends Comparable<T>> void run(JQueue<T> queue) {
            java.util.Stack<T> stack = new java.util.Stack<>();
            while (!queue.isEmpty()) {
                stack.push(queue.poll());
            }
            while (!stack.isEmpty()) {
                queue.offer(stack.pop());
            }
        }
    },

    ROTATE_ONCE {
        public <T extends Comparable<T>> void run(JQueue<T> queue) {
            if (queue.isEmpty()) return;
            queue.offer(queue.poll());
        }
    },

    DUPLICATE_ALL {
        public <T extends Comparable<T>> void run(JQueue<T> queue) {
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                T val = queue.poll();
                queue.offer(val);
                queue.offer(val);
            }
        }
    },

    REMOVE_DUPLICATES {
        public <T extends Comparable<T>> void run(JQueue<T> queue) {
            java.util.HashSet<T> seen = new java.util.HashSet<>();
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                T val = queue.poll();
                if (!seen.contains(val)) {
                    seen.add(val);
                    queue.offer(val);
                }
            }
        }
    },

    SORT_QUEUE {
        public <T extends Comparable<T>> void run(JQueue<T> queue) {
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                T min = null;
                for (int j = 0; j < queue.size(); j++) {
                    T curr = queue.poll();
                    if (min == null || curr.compareTo(min) < 0) {
                        if (min != null) queue.offer(min);
                        min = curr;
                    } else {
                        queue.offer(curr);
                    }
                }
                queue.offer(min);
            }
        }
    };

    public abstract <T extends Comparable<T>> void run(JQueue<T> queue);
}
