package com.redspace.utils.javabreaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility methods for 'breaking' Java protection/encapsulation using reflection.  For those odd times when you
 * really have to get something done and just can't do it the right way.  Particularly useful when working
 * third party libraries around.
 * <p>
 * Instances may use caching to accelerate repeat operations.  No guarantees, but drop references when you know
 * you're done.
 */
@SuppressWarnings("unused")
public interface JavaBreaker {
    static JavaBreaker create() { return new JavaBreakerImpl(); }

    /**
     * Finds the named method accepting the given argument types in the given class.  Doesn't find inherited methods.
     * Doesn't care about visibility or anything else.
     * <p>
     * Just like regular reflection, argument types are non-polymorphic (i.e.
     * {@code declaredMethod(List.class, "add", String.class) } won't find
     * {@link java.util.List#add(Object)})
     *
     * @param clazz     The class to search
     * @param name      The method name to find
     * @param arguments The argument types that this method takes
     * @return The located method.
     * @throws SecurityException   if the JVM is restrictive enough to prevent this type of thing.
     * @throws ReflectionException if the method could not be found.
     */
    Method declaredMethod(Class<?> clazz, String name, Class<?>... arguments);

    /**
     * Like {@link #declaredMethod(Class, String, Class[])} but returns null if the method couldn't be found.
     */
    Method declaredMethodOrNull(Class<?> clazz, String name, Class<?>... arguments);

    /**
     * Finds and invokes the given method on the given object with the given arguments, using reflection throughout.  Mostly the
     * same semantics as {@link #declaredMethod(Class, String, Class[])} except that it uses concrete arguments, and also runs
     * the method if it was located.  Performs a quiet cast on the result.  If the invoked method doesn't return, this will
     * return null (and T should be given as Void).
     * <p>
     * One important caveat is that arguments have to match the located method non-polymorphically in order to apply. So if the
     * method takes {@link CharSequence}, but you pass a {@link String} instance, it won't be located.  This excludes a lot of
     * methods, but would be very performance-averse to resolve.
     * <p>
     * Because of how varargs methods work, this method also won't catch methods whose arguments are primitives (such as
     * {@link java.util.List#listIterator(int)}).  For this case, do something like
     * {@code declaredMethod(List.class, "listIterator", int.class).invoke(7)}.
     * <p>
     * Treat this method like a nice simple way to do something you know will work.  Probably not well suited for production code.
     *
     * @param receiver  The object to run the method on
     * @param name      The name of the method to run
     * @param arguments The arguments to locate/execute the method with
     * @param <T>       The type to expect as the return type.
     * @return Whatever the underlying method returns, cast to {@link T}.
     * @throws ReflectionException if underlying reflective operations fail.
     */
    <T> T invoke(Object receiver, String name, Object... arguments);

    /**
     * Thrown by methods in this class when checked exceptions propagate from java.lang.reflect methods.
     */
    class ReflectionException extends RuntimeException {
        private static final long serialVersionUID = -6425868492508213255L;

        ReflectionException(Exception cause) {
            super(cause);
        }
    }
}

class JavaBreakerImpl implements JavaBreaker {
    @Override
    public Method declaredMethod(Class<?> clazz, String name, Class<?>... arguments) {
        try {
            return baseDeclaredMethod(clazz, name, arguments);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Method declaredMethodOrNull(Class<?> clazz, String name, Class<?>... arguments) {
        try {
            return baseDeclaredMethod(clazz, name, arguments);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private Method baseDeclaredMethod(Class<?> clazz, String name, Class<?>... arguments) throws NoSuchMethodException {
        final Method out = clazz.getDeclaredMethod(name, arguments);
        out.setAccessible(true);
        return out;
    }

    @Override
    @SuppressWarnings({"WeakerAccess", "unused"})
    public <T> T invoke(Object receiver, String name, Object... arguments) {
        final Class<?>[] argTypes = arguments == null ? null : new Class<?>[arguments.length];

        if (arguments != null)
            for (int i = 0; i < arguments.length; i++)
                argTypes[i] = arguments[i].getClass();

        try {
            // noinspection unchecked
            return (T) declaredMethod(receiver.getClass(), name, argTypes).invoke(receiver, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

}
