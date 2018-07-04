package com.redspace.utils.javabreaker;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JavaBreakerTest {
    private JavaBreaker subject;

    @Before
    public void before() {
        subject = JavaBreaker.create();
    }

    @Test
    public void declaredMethodShouldFindNoArgMethod() {
        assertThat(subject.declaredMethod(List.class, "size")).isNotNull();
    }

    @Test
    public void declaredMethodShouldFindMultiArgMethod() {
        assertThat(subject.declaredMethod(List.class, "add", int.class, Object.class)).isNotNull();
    }

    @Test
    public void declaredMethodShouldThrowWhenNotFound() {
        assertThatExceptionOfType(JavaBreaker.ReflectionException.class)
                .isThrownBy(() -> assertThat(subject.declaredMethod(List.class, "add", String.class)));
    }

    @Test
    public void declaredMethodOrNullShouldReturnNullWhenNotFound() {
        assertThat(subject.declaredMethodOrNull(List.class, "bad", int.class)).isNull();
    }

    @Test
    public void invokeShouldInvokeMultiArgMethods() {
        assertThat(subject.<Integer>invoke(new LinkedList<>(), "indexOf", new Object())).isEqualTo(-1);
    }
}
