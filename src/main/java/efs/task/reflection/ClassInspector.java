package efs.task.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ClassInspector {

  /**
   * Metoda powinna wyszukać we wszystkich zadeklarowanych przez klasę polach te które oznaczone
   * są adnotacją podaną jako drugi parametr wywołania tej metody. Wynik powinien zawierać tylko
   * unikalne nazwy pól (bez powtórzeń).
   *
   * @param type       klasa (typ) poddawana analizie
   * @param annotation szukana adnotacja
   * @return lista zawierająca tylko unikalne nazwy pól oznaczonych adnotacją
   */
  public static Collection<String> getAnnotatedFields(final Class<?> type,
                                                      final Class<? extends Annotation> annotation) {
    List<String> annotatedFields = new ArrayList<>();

    Field[] fields = type.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(annotation)) {
        annotatedFields.add(field.getName());
      }
    }

    return annotatedFields;
  }

  /**
   * Metoda powinna wyszukać wszystkie zadeklarowane bezpośrednio w klasie metody oraz te
   * implementowane przez nią pochodzące z interfejsów, które implementuje. Wynik powinien zawierać
   * tylko unikalne nazwy metod (bez powtórzeń).
   *
   * @param type klasa (typ) poddawany analizie
   * @return lista zawierająca tylko unikalne nazwy metod zadeklarowanych przez klasę oraz te
   * implementowane
   */
  public static Collection<String> getAllDeclaredMethods(final Class<?> type) {
    Set<String> allMethods = new HashSet<>();

    Method[] declaredMethods = type.getDeclaredMethods();
    for (Method method : declaredMethods) {
      allMethods.add(method.getName());
    }

    Class<?>[] interfaces = type.getInterfaces();
    for (Class<?> implementedInterface : interfaces) {
      Method[] interfaceMethods = implementedInterface.getDeclaredMethods();
      for (Method method : interfaceMethods) {
        allMethods.add(method.getName());
      }
    }

    return allMethods;
  }

  /**
   * Metoda powinna odszukać konstruktor zadeklarowany w podanej klasie który przyjmuje wszystkie
   * podane parametry wejściowe. Należy tak przygotować implementację aby nawet w przypadku gdy
   * pasujący konstruktor jest prywatny udało się poprawnie utworzyć nową instancję obiektu
   * <p>
   * Przykładowe użycia:
   * <code>ClassInspector.createInstance(Villager.class)</code>
   * <code>ClassInspector.createInstance(Villager.class, "Nazwa", "Opis")</code>
   *
   * @param type klasa (typ) którego instancje ma zostać utworzona
   * @param args parametry które mają zostać przekazane do konstruktora
   * @return nowa instancja klasy podanej jako parametr zainicjalizowana podanymi parametrami
   * @throws Exception wyjątek spowodowany nie znalezieniem odpowiedniego konstruktora
   */
  public static <T> T createInstance(final Class<T> type, final Object... args) throws Exception {
    Constructor<?> constructor = findMatchingConstructor(type, args);
    constructor.setAccessible(true);
    return (T) constructor.newInstance(args);
  }

  private static Constructor<?> findMatchingConstructor(Class<?> type, Object[] args) throws NoSuchMethodException {
    Constructor<?>[] constructors = type.getDeclaredConstructors();
    for (Constructor<?> constructor : constructors) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      if (isMatchingArguments(parameterTypes, args)) {
        return constructor;
      }
    }
    throw new NoSuchMethodException("Matching constructor not found");
  }

  private static boolean isMatchingArguments(Class<?>[] parameterTypes, Object[] args) {
    if (parameterTypes.length != args.length) {
      return false;
    }

    for (int i = 0; i < parameterTypes.length; i++) {
      if (!parameterTypes[i].isInstance(args[i])) {
        return false;
      }
    }

    return true;
  }
}
