package com.epam.mentoring.algorithm.tree;

public class TreeService {

    private Node head;

    public TreeService() {
    }

    public Node add(int value) {
        if (head == null) {
            head = new Node(value);
            return head;
        }
        return addRecursively(head, value);
    }

    public Node search(int value) {
        return searchRecursively(head, value);
    }

    private Node addRecursively(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }

        if (value < current.getValue()) {
            current.setLeft(addRecursively(current.getLeft(), value));
        }
        if (value > current.getValue()) {
            current.setRight(addRecursively(current.getRight(), value));
        }

        return current;
    }

    private Node searchRecursively(Node current, int value) {
        Node node = null;
        if (current != null) {
            if (value < current.getValue()) {
                node = searchRecursively(current.getLeft(), value);
            } else if (value > current.getValue()) {
                node = searchRecursively(current.getRight(), value);
            } else {
                return current;
            }
        }
        return node;
    }

}
