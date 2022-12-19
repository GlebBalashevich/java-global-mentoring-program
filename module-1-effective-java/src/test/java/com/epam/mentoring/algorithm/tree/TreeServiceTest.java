package com.epam.mentoring.algorithm.tree;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreeServiceTest {

    private TreeService treeService;

    @BeforeEach
    void init() {
        treeService = new TreeService();
    }

    @Test
    void testAdd() {
        Node actual = treeService.add(4);
        treeService.add(5);
        treeService.add(3);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getValue()).isEqualTo(4);
        Assertions.assertThat(actual.getLeft().getValue()).isEqualTo(3);
        Assertions.assertThat(actual.getRight().getValue()).isEqualTo(5);
    }

    @Test
    void testSearchValueFound() {
        Node root = treeService.add(4);
        treeService.add(5);
        treeService.add(3);
        treeService.add(1);
        treeService.add(8);
        treeService.add(2);
        treeService.add(14);
        treeService.add(12);

        Node actual = treeService.search(12);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isEqualTo(root.getRight().getRight().getRight().getLeft());
    }

    @Test
    void testSearchValueNotFound() {
        treeService.add(4);
        treeService.add(5);
        treeService.add(3);
        treeService.add(1);
        treeService.add(8);
        treeService.add(12);
        treeService.add(2);
        treeService.add(14);

        Node actual = treeService.search(13);

        Assertions.assertThat(actual).isNull();
    }

}
