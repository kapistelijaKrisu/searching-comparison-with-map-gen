package model.structure;

/**
 * Last-in-first-out classic Stack implementation.
 * Only head is known and nodes are connected by reference.
 * @param <T> object type to hold.
 */
public class LIFOStack<T> {

    private static class Node<T> {
        private final T value;
        private Node<T> next;

        private Node(T val) {
            this.value = val;
            this.next = null;
        }

        private T getValue() {
            return value;
        }

        private Node<T> getNext() {
            return next;
        }

        private void setNext(Node<T> nextNode) {
            this.next = nextNode;
        }
    }

    private Node<T> head;

    /**
     * Creates empty stack where head is null.
     */
    public LIFOStack() {
        head = null;
    }

    /**
     * Adds given object as a head to stack and connects it with previous head by reference.
     * @param value object to hold (new head of the stack)
     */
    public void push(T value) {
        Node<T> newHead = new Node<>(value);
        newHead.setNext(head);
        head = newHead;
    }

    /**
     * Removes current head of the stack and returns it.
     * @return deleted head
     */
    public T pop() {
        if (isEmpty()) return null;
        T polledValue = head.getValue();
        head = head.getNext();
        return polledValue;
    }

    /**
     * Returns whether the stack contains any nodes or not
     * @return if head is null true else false
     */
    public boolean isEmpty() {
        return head == null;
    }



}