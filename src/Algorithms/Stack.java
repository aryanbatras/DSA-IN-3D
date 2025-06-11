package Algorithms;

import Collections.JStack;
import Rendering.*;

public enum Stack {
    BALANCED_PARENTHESIS {
        public void run(JStack stack) {
            String expr = "(())(()())";
            for (char ch : expr.toCharArray()) {
                if (ch == '(') {
                    stack.push(1); // Push for '('
                } else if (ch == ')') {
                    if (!stack.isEmpty()) stack.pop(); // Pop for ')'
                }
            }
        }
    },

    BINARY_TO_DECIMAL {
        public void run(JStack stack) {
            int number = 13;
            while (number > 0) {
                stack.push(number % 2);
                number /= 2;
            }

            int result = 0, power = 1;
            while (!stack.isEmpty()) {
                result += stack.pop() * power;
                power *= 2;
            }

            stack.push(result);
        }
    },

    REVERSE_STACK {
        public void run(JStack stack) {
            JStack temp = new JStack();
            while (!stack.isEmpty()) temp.push(stack.pop());
            while (!temp.isEmpty()) stack.push(temp.pop());
        }
    },

    NEXT_GREATER_ELEMENT {
        public void run(JStack stack) {
            int[] input = {4, 5, 2, 10};
            for (int i = input.length - 1; i >= 0; i--) {
                int current = input[i];
                while (!stack.isEmpty() && stack.peek() <= current) {
                    stack.pop();
                }
                stack.push(current);
            }
        }
    },

    SORT_STACK {
        public void run(JStack stack) {
            JStack temp = new JStack();
            while (!stack.isEmpty()) {
                int val = stack.pop();
                while (!temp.isEmpty() && temp.peek() > val) {
                    stack.push(temp.pop());
                }
                temp.push(val);
            }
            while (!temp.isEmpty()) stack.push(temp.pop());
        }
    };

    public abstract void run(JStack stack);
}
