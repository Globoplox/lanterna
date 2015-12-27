package com.googlecode.lanterna.gui2;

/**
 * This {@code TextGUIThread} implementation is assuming the GUI event thread will be the same as the thread that
 * creates the {@TextGUI} objects. This means on the thread you create the GUI on, when you are done you pass over
 * control to lanterna and let it manage the GUI for you. When the GUI is done, you'll get back control again over the
 * thread. This is different from {@code SeparateTextGUIThread} which spawns a new thread that manages the GUI and
 * leaves the current thread for you to handle.<p/>
 * Here are two examples of how to use {@code SameTextGUIThread}:
 * <pre>
 *     {@code
 *     MultiWindowTextGUI textGUI = new MultiWindowTextGUI(new SameTextGUIThread.Factory(), screen);
 *     // ... add components ...
 *     while(weWantToContinueRunningTheGUI) {
 *         if(!textGUI.getGUIThread().processEventsAndUpdate()) {
 *             Thread.sleep(1);
 *         }
 *     }
 *     // ... tear down ...
 *     }
 * </pre>
 * In the example above, we use very precise control over events processing and when to update the GUI. In the example
 * below we pass some of that control over to Lanterna, since the thread won't resume until the window is closed.
 * <pre>
 *     {@code
 *     MultiWindowTextGUI textGUI = new MultiWindowTextGUI(new SameTextGUIThread.Factory(), screen);
 *     Window window = new MyWindow();
 *     textGUI.addWindowAndWait(window); // This call will run the event/update loop and won't return until "window" is closed
 *     // ... tear down ...
 *     }
 * </pre>
 * @see SeparateTextGUIThread
 * @see TextGUIThread
 */
public class SameTextGUIThread extends AbstractTextGUIThread {

    private final Thread guiThread;

    private SameTextGUIThread(TextGUI textGUI) {
        super(textGUI);
        guiThread = Thread.currentThread();
    }

    @Override
    public Thread getThread() {
        return guiThread;
    }

    @Override
    public void invokeAndWait(Runnable runnable) throws IllegalStateException, InterruptedException {
        if(guiThread == null || guiThread == Thread.currentThread()) {
            runnable.run();
        }
        super.invokeAndWait(runnable);
    }

    /**
     * Default factory class for {@code SameTextGUIThread}, you need to pass this to the {@code TextGUI} constructor if
     * you want it to use this class
     */
    public static class Factory implements TextGUIThreadFactory {
        @Override
        public TextGUIThread createTextGUIThread(TextGUI textGUI) {
            return new SameTextGUIThread(textGUI);
        }
    }
}
