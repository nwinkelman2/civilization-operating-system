package io.github.opencivilizationplatform.saga;

public interface SagaStep<T> {
    void execute(T context);
    void compensate(T context);
    String getName();
}
