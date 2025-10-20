package wbs.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MapStream<K, V> implements Stream<Map.Entry<K, V>> {
    private final Stream<Map.Entry<K, V>> stream;
    public MapStream(Map<K, V> map) {
        stream = map.entrySet().stream();
    }
    public MapStream(Stream<Map.Entry<K, V>> stream) {
        this.stream = stream;
    }

    @Override
    public MapStream<K, V> filter(Predicate<? super Map.Entry<K, V>> predicate) {
        return new MapStream<>(stream.filter(predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super Map.Entry<K, V>, ? extends R> mapper) {
        return stream.map(mapper);
    }

    public MapStream<K, V> filter(BiPredicate<? super K, ? super V> predicate) {
        return new MapStream<>(stream.filter(entry -> predicate.test(entry.getKey(), entry.getValue())));
    }

    public <R> Stream<R> map(BiFunction<? super K, ? super V, ? extends R> mapper) {
        return stream.map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
    }

    public <R> MapStream<R, V> mapKey(Function<? super K, R> mapper) {
        return new MapStream<>(stream.map(
                entry -> new AbstractMap.SimpleEntry<>(mapper.apply(entry.getKey()), entry.getValue()))
        ).distinct();
    }

    public <R> MapStream<K, R> mapValue(Function<? super V, R> mapper) {
        return new MapStream<>(stream.map(
                entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), mapper.apply(entry.getValue()))
                )
        ).distinct();
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Map.Entry<K, V>> mapper) {
        return stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Map.Entry<K, V>> mapper) {
        return stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Map.Entry<K, V>> mapper) {
        return stream.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Map.Entry<K, V>, ? extends Stream<? extends R>> mapper) {
        return stream.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Map.Entry<K, V>, ? extends IntStream> mapper) {
        return stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Map.Entry<K, V>, ? extends LongStream> mapper) {
        return stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Map.Entry<K, V>, ? extends DoubleStream> mapper) {
        return stream.flatMapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> mapMulti(BiConsumer<? super Map.Entry<K, V>, ? super Consumer<R>> mapper) {
        return stream.mapMulti(mapper);
    }

    @Override
    public IntStream mapMultiToInt(BiConsumer<? super Map.Entry<K, V>, ? super IntConsumer> mapper) {
        return stream.mapMultiToInt(mapper);
    }

    @Override
    public LongStream mapMultiToLong(BiConsumer<? super Map.Entry<K, V>, ? super LongConsumer> mapper) {
        return stream.mapMultiToLong(mapper);
    }

    @Override
    public DoubleStream mapMultiToDouble(BiConsumer<? super Map.Entry<K, V>, ? super DoubleConsumer> mapper) {
        return stream.mapMultiToDouble(mapper);
    }

    @Override
    public MapStream<K, V> distinct() {
        return new MapStream<>(stream.distinct());
    }

    @Override
    public MapStream<K, V> sorted() {
        return new MapStream<>(stream.sorted());
    }

    @Override
    public MapStream<K, V> sorted(Comparator<? super Map.Entry<K, V>> comparator) {
        return new MapStream<>(stream.sorted(comparator));
    }

    @Override
    public MapStream<K, V> peek(Consumer<? super Map.Entry<K, V>> action) {
        return new MapStream<>(stream.peek(action));
    }

    @Override
    public MapStream<K, V> limit(long maxSize) {
        return new MapStream<>(stream.limit(maxSize));
    }

    @Override
    public MapStream<K, V> skip(long n) {
        return new MapStream<>(stream.skip(n));
    }

    @Override
    public MapStream<K, V> takeWhile(Predicate<? super Map.Entry<K, V>> predicate) {
        return new MapStream<>(stream.takeWhile(predicate));
    }

    @Override
    public MapStream<K, V> dropWhile(Predicate<? super Map.Entry<K, V>> predicate) {
        return new MapStream<>(stream.dropWhile(predicate));
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Map.Entry<K, V>> action) {
        stream.forEachOrdered(action);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        stream.forEach(val -> action.accept(val.getKey(), val.getValue()));
    }

    public void forEachOrdered(BiConsumer<? super K, ? super V> action) {
        stream.forEachOrdered(val -> action.accept(val.getKey(), val.getValue()));
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return stream.toArray();
    }

    @Override
    public @NotNull <A> A @NotNull [] toArray(IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    @Override
    public Map.Entry<K, V> reduce(Map.Entry<K, V> identity, BinaryOperator<Map.Entry<K, V>> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public @NotNull Optional<Map.Entry<K, V>> reduce(BinaryOperator<Map.Entry<K, V>> accumulator) {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Map.Entry<K, V>, U> accumulator, BinaryOperator<U> combiner) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Map.Entry<K, V>> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Map.Entry<K, V>, A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public List<Map.Entry<K, V>> toList() {
        return stream.toList();
    }

    @Override
    public @NotNull Optional<Map.Entry<K, V>> min(Comparator<? super Map.Entry<K, V>> comparator) {
        return stream.min(comparator);
    }

    @Override
    public @NotNull Optional<Map.Entry<K, V>> max(Comparator<? super Map.Entry<K, V>> comparator) {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Map.Entry<K, V>> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public @NotNull Optional<Map.Entry<K, V>> findFirst() {
        return stream.findFirst();
    }

    @Override
    public @NotNull Optional<Map.Entry<K, V>> findAny() {
        return stream.findAny();
    }

    public static <T> Builder<T> builder() {
        return Stream.builder();
    }

    public static @NotNull <T> Stream<T> empty() {
        return Stream.empty();
    }

    public static @NotNull <T> Stream<T> of(T t) {
        return Stream.of(t);
    }

    public static <T> Stream<T> ofNullable(T t) {
        return Stream.ofNullable(t);
    }

    @SafeVarargs
    public static @NotNull <T> Stream<T> of(T... values) {
        return Stream.of(values);
    }

    public static @NotNull <T> Stream<T> iterate(T seed, UnaryOperator<T> f) {
        return Stream.iterate(seed, f);
    }

    public static @NotNull <T> Stream<T> iterate(T seed, @NotNull Predicate<? super T> hasNext, @NotNull UnaryOperator<T> next) {
        return Stream.iterate(seed, hasNext, next);
    }

    public static @NotNull <T> Stream<T> generate(@NotNull Supplier<? extends T> s) {
        return Stream.generate(s);
    }

    public static @NotNull <T> Stream<T> concat(@NotNull Stream<? extends T> a, @NotNull Stream<? extends T> b) {
        return Stream.concat(a, b);
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
        return stream.iterator();
    }

    @Override
    public @NotNull Spliterator<Map.Entry<K, V>> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public @NotNull MapStream<K, V> sequential() {
        return new MapStream<>(stream.sequential());
    }

    @Override
    public @NotNull MapStream<K, V> parallel() {
        return new MapStream<>(stream.parallel());
    }

    @Override
    public @NotNull MapStream<K, V> unordered() {
        return new MapStream<>(stream.unordered());
    }

    @Override
    public @NotNull MapStream<K, V> onClose(@NotNull Runnable closeHandler) {
        return new MapStream<>(stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        stream.close();
    }

    public Map<K, V> toMap() {
        return stream.collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> v1,
                LinkedHashMap::new
        ));
    }
}

