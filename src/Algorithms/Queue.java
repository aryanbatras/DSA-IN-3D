package Algorithms;

import Collections.JQueue;
import java.util.LinkedList;
import java.util.Stack;

public enum Queue {

    REVERSE_ENTIRE {
        public void run(JQueue queue) {
            Stack<Integer> stack = new Stack<>();
            while (!queue.isEmpty()) {
                stack.push(queue.poll());
            }
            while (!stack.isEmpty()) {
                queue.offer(stack.pop());
            }
        }
    },

    ROTATE_ONCE {
        public void run(JQueue queue) {
            if (queue.isEmpty()) return;
            int front = queue.poll();
            queue.offer(front);
        }
    },

    INTERLEAVE_HALVES {
        public void run(JQueue queue) {
            int n = queue.size();
            if (n % 2 != 0) return;

            java.util.Queue<Integer> firstHalf = new LinkedList<>();
            for (int i = 0; i < n / 2; i++) {
                firstHalf.add(queue.poll());
            }

            while (!firstHalf.isEmpty()) {
                queue.offer(firstHalf.poll());
                queue.offer(queue.poll());
            }
        }
    },

    DUPLICATE_ALL {
        public void run(JQueue queue) {
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                int val = queue.poll();
                queue.offer(val);
                queue.offer(val);
            }
        }
    },

    REMOVE_ODDS {
        public void run(JQueue queue) {
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                int val = queue.poll();
                if (val % 2 == 0) {
                    queue.offer(val);
                }
            }
        }
    };

    public abstract void run(JQueue queue);
}
