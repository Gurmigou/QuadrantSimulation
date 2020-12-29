package Multithreading.ThreadsLearning.NaturalSelection.Utilities;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  This is a class that helps to synchronize the time at different threads.
 *  <p>When the first thread comes into a method, it calls a method {@code enter},
 *     which sets a {@code timeEntered} value. The other threads (which come into
 *     a method a bit later) can't modify this value.</p>
 *  <p>A method {@code getTimeEntered} should be called to get a time which a first
 *     entered thread has set.</p>
 *  <p>When execution of a method ends, the first ended thread calls a method {@code close}
 *     that zeros a {@code timeEntered} value.</p>
 *  <p>If a method {@code getTimeEntered} is called when {@code timeEntered} value
 *     is equal to zero, then the thread will wait until the value {@code timeEntered}
 *     is changed by another thread.</p>
 */
public class TimeSyn {
    /**
     *  A value of time set by the first entered thread.
     */
    private static volatile long timeEntered = 0;

    /**
     *  A {@code timeEntered} value can't be changed when this flag is <i>true</i>.
     */
    private static final AtomicBoolean flag = new AtomicBoolean(false);

    /**
     *  This method sets a new {@code timeEntered} value. Moreover, it changes a
     *  a {@code flag} value to <i>true</i>.
     */
    public static void enter() {
        if (!flag.getAndSet(true)) {
            timeEntered = System.currentTimeMillis();
        }
    }

    /**
     *  This method zeros a {@code timeEntered} value. Moreover, it changes a
     *  a {@code flag} value to <i>false</i>.
     */
    public static void close() {
        if (flag.getAndSet(false)) {
            timeEntered = 0;
        }
    }

    /**
     * This is a getter of {@code timeEntered} value.
     * <p>A thread which calls a method {@code onSpinWait()} if {@code timeEntered}
     *    value is equal to zero. A thread won't fall asleep and the data in the CPU won't
     *    be re-cached. In other words, the thread will perform the empty iterations until
     *    {@code timEntered} value is changed by another thread.</p>
     * @return a time set by a first-entered thread.
     */
    public static long getTimeEntered() {
        while (timeEntered == 0) {
            /*
             *  Использования busy spin подхода чтобы сохранить кэш-процессора.
             *  Методы ожидания sleep(), wait() и т.п. вынуждают поток переходить
             *  в состояние ожидания. После выхода из него, поток может продолжить
             *  свое выполнение уже на другом ядре, при этом понадобится произвести
             *  перестройку кэша (при переходе из одного ядра на другое).
             *  Использование busy spin подхода не имеет таких эффектов, тоесть
             *  поток не отратит процессорное время, а будет попросту выполнять
             *  пустые итерации до тех пор, пока в условии цикла не будет выполнено
             *  подходящее условие.
             */
            Thread.onSpinWait();
        }
        return timeEntered;
    }
}
