package org.waraccademy.posta.utils;



import java.util.Objects;

public class Triple<E> {
    private final E first,second,third;

    public Triple(E first, E second, E third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public E getFirst(){
        return first;
    }

    public E getSecond(){
        return second;
    }

    public E getThird(){
        return third;
    }

    public static <E> Triple<E> of(E first, E second, E third){
        return new Triple<>(first,second,third);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?> triple = (Triple<?>) o;
        return Objects.equals(first, triple.first) && Objects.equals(second, triple.second) && Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
