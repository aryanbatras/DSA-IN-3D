package Algorithms;

import Collections.JStack;

public enum Stack {

    REVERSE_STACK {
        public <T extends Comparable<T>> void run(JStack<T> stack) {
            JStack<T> temp = new JStack<T>();
            while (!stack.isEmpty()) {
                temp.push( stack.pop());
            }
            while (!temp.isEmpty()) {
                stack.push(temp.pop());
            }
        }
    },

    SORT_STACK {
        public <T extends Comparable<T>> void run(JStack<T> stack) {
            JStack<T> temp = new JStack<T>();
            while (!stack.isEmpty()) {
                T val = stack.pop();
                while (!temp.isEmpty() && temp.peek().compareTo(val) > 0) {
                    stack.push(temp.pop());
                }
                temp.push(val);
            }
            while (!temp.isEmpty()) {
                stack.push(temp.pop());
            }
        }
    },

    DUPLICATE_REMOVAL {
        public <T extends Comparable<T>> void run(JStack<T> stack) {
            JStack<T> temp = new JStack<T>();
            while (!stack.isEmpty()) {
                T current = stack.pop();
                boolean exists = false;
                JStack<T> checker = new JStack<T>();
                while (!temp.isEmpty()) {
                    T val = temp.pop();
                    if (val.compareTo(current) == 0) exists = true;
                    checker.push(val);
                }
                while (!checker.isEmpty()) {
                    temp.push(checker.pop());
                }
                if (!exists) {
                    temp.push(current);
                }
            }
            while (!temp.isEmpty()) {
                stack.push(temp.pop());
            }
        }
    },

    FIND_MINIMUM {
        public <T extends Comparable<T>> void run(JStack<T> stack) {
            if (stack.isEmpty()) return;
            T min = stack.pop();
            JStack<T> temp = new JStack<T>();
            temp.push(min);

            while (!stack.isEmpty()) {
                T current = stack.pop();
                if (current.compareTo(min) < 0) {
                    min = current;
                }
                temp.push(current);
            }

            while (!temp.isEmpty()) {
                stack.push(temp.pop());
            }

            stack.push(min);
        }
    },

    FIND_MAXIMUM {
        public <T extends Comparable<T>> void run(JStack<T> stack) {
            if (stack.isEmpty()) return;
            T max = stack.pop();
            JStack<T> temp = new JStack<T>();
            temp.push(max);

            while (!stack.isEmpty()) {
                T current = stack.pop();
                if (current.compareTo(max) > 0) {
                    max = current;
                }
                temp.push(current);
            }

            while (!temp.isEmpty()) {
                stack.push(temp.pop());
            }

            stack.push(max);
        }
    };

    public abstract <T extends Comparable<T>> void run(JStack<T> stack);
}
