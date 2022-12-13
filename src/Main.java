import java.util.Arrays;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws RuntimeException {
    Scanner scan = new Scanner(System.in);

    System.out.println(
        "Введите выражение в формате a + b (вместо \"+\" - сложения, можно использовать \"/\" - деление, \"*\" - умножение, \"-\" - вычитание ):");

    String ex = scan.nextLine();

    // Проверка на пустую строку
    if (ex == null || ex.isEmpty()) {
      throw new RuntimeException("Введена пустая строка!");
    }

    // Вызов метода класса калькулятор
    String result = Calculator.calc(ex.replaceAll("\\s+", ""));

    System.out.println("Результат: " + result); // Вывод результата
  }

}

class Calculator {

  static String calc(String input) throws RuntimeException {
    String[] regexActions = {"\\+", "-", "/", "\\*"};
    String[] actions = {"+", "-", "/", "*"};
    int actionIndex = -1;
    int result = 0;

    int a;
    int b;

    // Ищем арефметический знак в строке из доступных
    for (int i = 0; i < actions.length; i++) {
      if (input.contains(actions[i]) && i != 1) {
        actionIndex = i;
        break;
      }
    }

    if (actionIndex == -1) {
      throw new RuntimeException("Введено некорректное выражение!");
    }

    // Разбиваем строку на подстроки с числами с помощью найденого арефметического знака
    String[] data = input.split(regexActions[actionIndex]);

    if (data.length > 2) {
      throw new RuntimeException("Введено недопустимое количество аргументов выражения!");
    }

    // Сохраняем значение для дальнейшей конвертации из арабской в римскую систему
    boolean isRoman = Converter.isRomen(data[0]) && Converter.isRomen(data[1]);

    // Проверяем в какой системе исчесления находяться оба числа
    if (isRoman) {

      // Конвертируем из римской в арабскую систему
      a = Converter.converterRomenInArabian(data[0]);
      b = Converter.converterRomenInArabian(data[1]);
    } else if (Converter.isArabic(data[0]) && Converter.isArabic(data[1])) {

      // Преобразуем строки в числа
      a = Integer.parseInt(data[0]);
      b = Integer.parseInt(data[1]);
    } else {
      throw new RuntimeException(
          "Введеные числа находятся в разных системах исчесления или указаны в некорректной системе исчесления!");
    }

    // Проверяем числа на корректный допустимый диапазон
    if (!((a >= 1 && a <= 10) && (b >= 1 && b <= 10))) {
      throw new RuntimeException("Введеные числа находятся в допустимом деапозоне!");
    }

    // Вычесляем результат
    switch (actions[actionIndex]) {
      case "+":
        result = a + b;
        break;
      case "-":
        result = a - b;
        break;
      case "/":
        result = a / b;
        break;
      case "*":
        result = a * b;
        break;
    }

    // Смотрим в какой системе исчесления число было изначально
    if (isRoman) {

      // Проверяем на отрицательное число в римской системе исчесления
      if (result <= 0) {
        throw new RuntimeException("В римской системе нет отрицательных чисел!");
      }

      // Проверяем на максимально разрешенное число в римской системе
      if (result >= 4000) {
        throw new RuntimeException(
            "Результат превысел максимально разрешенный диапозон в римской системе исчесления!");
      }
      return Converter.converterArabianInRoman(result); // Возвращаем римское число
    } else {
      return String.valueOf(result); // Возвращаем арабское число
    }
  }
}

class Converter {

  static int[] keyRoman = {1, 5, 10, 50, 100, 500, 1000};
  static char[] valRoman = {'I', 'V', 'X', 'L', 'C', 'D', 'M'};

  static int[] keyRomanPattern = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
  static String[] valRomanPattern = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D",
      "CM", "M"};

  // Метод для конвертации римского числа в арабскую систему исчесления
  static int converterRomenInArabian(String s) {

    int end = s.length() - 1;
    char[] arr = s.toCharArray(); // Разбиваем римское число на числа
    int[] arabian = new int[0];

    // Находим по отдельности орабские числа
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < keyRoman.length; j++) {
        if (arr[i] == valRoman[j]) {
          arabian = append(arabian, keyRoman[j]);
          break;
        }
      }
    }

    int result = arabian[end]; // Присваиваем последнее найденое число в массиве

    // Вычисляем арабское число
    for (int i = end - 1; i >= 0; i--) {
      if (arabian[i] < arabian[i + 1]) {
        result -= arabian[i];
      } else {
        result += arabian[i];
      }
    }

    return result;
  }

  // Метод для конвертации арабского числа в римскую систему исчесления
  static String converterArabianInRoman(int arabian) {

    String result = "";
    int arabianKey = 0;

    // Работает пока арабское число не будет равно 0
    do {

      // Находим индекс римского числа
      for (int i = keyRomanPattern.length - 1; i >= 0; i--) {
        if (keyRomanPattern[i] <= arabian) {
          arabianKey = i;
          break;
        }
      }

      // Вычисляем римское число
      arabian -= keyRomanPattern[arabianKey];
      result += valRomanPattern[arabianKey];
    } while (arabian != 0);

    return result;
  }

  // Метод для проверки на римское число
  static boolean isRomen(String s) {
    char[] arr = s.toCharArray();
    int count = 0;

    // Проверяем на корректность символов в строке которая является одним числом
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < valRoman.length; j++) {
        if (arr[i] == valRoman[j]) {
          ++count;
        }
      }
    }

    // count должен быть равен длине строки
    if (count == arr.length) {
      return true;
    }

    return false;
  }

  // Метод для проверки на арабское число
  static boolean isArabic(String s) {
    return s.matches("(-?[0-9]+)");
  }


  // Метод для добавления элемента в массив
  static int[] append(int[] arr, int a) {
    // Создаем новый массив
    int[] newArray = Arrays.copyOf(arr, arr.length + 1);
    newArray[arr.length] = a; // Добавляем элемент в конец массива
    return newArray;
  }
}


