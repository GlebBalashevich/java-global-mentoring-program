package com.epam.mentoring.algorithm.tree;

import java.util.Objects;

import com.epam.mentoring.util.Generated;

public class Node {

    private int value;

    private Node left;

    private Node right;

    public Node(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Node node = (Node) o;

        if (value != node.value)
            return false;
        if (!Objects.equals(left, node.left))
            return false;
        return Objects.equals(right, node.right);
    }

    @Generated
    @Override
    public int hashCode() {
        int result = value;
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

}
