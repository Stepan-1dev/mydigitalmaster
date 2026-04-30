package org.example;

public class Main{
    static void main() {
        try {
            while (true) {
                System.out.println("Проверка проекта");
                Thread.sleep(1005);
            }
        } catch (InterruptedException e){
            System.out.println("Ошибка!");
        }
    }
}
