import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Prosta klasa TaskManager zarządzająca listą zadań.
 */
public class TaskManager {

    public static class Task {
        private String name;
        private boolean completed;
        private int priority; // 1–5

        public Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
            this.completed = false;
        }

        public void complete() {
            this.completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return "[Task] " + name +
                    " | priority=" + priority +
                    " | completed=" + completed;
        }
    }

    private List<Task> tasks = new ArrayList<>();

    public void addTask(String name, int priority) {
        tasks.add(new Task(name, priority));
    }

    public void removeTask(String name) {
        tasks.removeIf(t -> t.name.equalsIgnoreCase(name));
    }

    public List<Task> getPending() {
        return tasks.stream()
                .filter(t -> !t.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getByPriority(int minPriority) {
        return tasks.stream()
                .filter(t -> t.getPriority() >= minPriority)
                .collect(Collectors.toList());
    }

    public void printAll() {
        tasks.forEach(System.out::println);
    }

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.addTask("Nauka Javy", 5);
        manager.addTask("Siłownia", 3);
        manager.addTask("Projekt na uczelnię", 4);

        manager.printAll();
        System.out.println("\nZadania o wysokim priorytecie:");
        manager.getByPriority(4).forEach(System.out::println);

        System.out.println("\nZadania nierozwiązane:");
        manager.getPending().forEach(System.out::println);

        manager.removeTask("Siłownia");
        System.out.println("\nPo usunięciu jednego zadania:");
        manager.printAll();
    }
}
