package de.sopro.logic;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private List<Node<T>> children = new ArrayList<>();
    private Node<T> parent = null;
    private T data;

    public Node(T data) {
        this.data = data;
    }

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }


    public List<Node<T>> getChildren() {
        return children;
    }

    public List<Node<T>> getLevel(int level) {
        if (level == 1) {
            return children;
        }

        List<Node<T>> result = new ArrayList<>();
        for (Node<T> child : children) {
            result.addAll(child.getLevel(level - 1));
        }
        return result;
    }

    public void addChildren(List<T> childs) {
        for (T child : childs) {
            this.addChild(child);
        }

    }

    public Node<T> getParent() {

        return parent;

    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public void addChild(T data) {
        Node<T> child = new Node<T>(data);
        child.setParent(this);
        this.children.add(child);
    }


    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return "\nthis is: " + data.toString() + "\nchilds are:" + children.toString() + "\nchilds ending";
    }
}
