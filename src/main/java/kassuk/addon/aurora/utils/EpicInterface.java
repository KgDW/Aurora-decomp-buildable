package kassuk.addon.aurora.utils;

@FunctionalInterface
public interface EpicInterface<T, E>
{
    E get(final T t);
}
