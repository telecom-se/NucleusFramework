package com.jcwhatever.bukkit.generic.performance.queued;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class QueueResult {

    private final Plugin _plugin;
    private final QueueTask _task;
    private final Object _sync = new Object();

    private final LinkedList<Runnable> _onComplete = new LinkedList<>();
    private final LinkedList<CancelHandler> _onCancel = new LinkedList<>();
    private final LinkedList<FailHandler> _onFail = new LinkedList<>();
    private final LinkedList<Runnable> _onEnd = new LinkedList<>();

    private String _cancelReason;
    private String _failReason;

    /**
     * Constructor.
     *
     * @param task  The task the results are for.
     */
    public QueueResult(QueueTask task) {
        PreCon.notNull(task);

        _plugin = task.getPlugin();
        _task = task;
    }

    /**
     * Get the owning task.
     */
    public QueueTask getTask() {
        return _task;
    }

    /**
     * Get a future to add future handlers.
     */
    public Future getFuture() {
        return new Future();
    }

    /**
     * Set task result and its child task results to completed.
     * Any cancel call backs are executed on the main thread and cleared.
     */
    void setComplete() {

        boolean isEmpty;

        synchronized (_sync) {
            isEmpty = _onComplete.isEmpty();
        }

        // run onComplete call backs
        if (!isEmpty) {
            Scheduler.runTaskSync(_plugin, new Runnable() {

                @Override
                public void run() {

                    synchronized (_sync) {
                        while (!_onComplete.isEmpty()) {

                            _onComplete.remove().run();
                        }
                    }
                }

            });
        }

        doOnEnd();
    }

    /**
     * Set task result and its child task results to cancelled.
     * Any cancel call backs are executed on the main thread and cleared.
     *
     * @param reason  Optionally add a message for why the task was cancelled.
     */
    void setCancelled(@Nullable final String reason) {

        _cancelReason = reason;

        boolean isEmpty;

        synchronized (_sync) {
            isEmpty = _onCancel.isEmpty();
        }

        // run onCancel call backs
        if (!isEmpty) {
            Scheduler.runTaskSync(_plugin, new Runnable() {

                @Override
                public void run() {

                    synchronized (_sync) {

                        while (!_onCancel.isEmpty()) {
                            _onCancel.remove().run(reason);
                        }
                    }
                }

            });
        }

        doOnEnd();
    }


    /**
     * Set task result and its child task results to failed.
     * Any fail call backs are executed on the main thread and cleared.
     */
    void setFailed(@Nullable final String reason) {

        _failReason = reason;

        boolean isEmpty;

        synchronized (_sync) {
            isEmpty = _onFail.isEmpty();
        }

        //run onFail call backs
        if (!isEmpty) {
            Scheduler.runTaskSync(_plugin, new Runnable() {

                @Override
                public void run() {

                    synchronized (_sync) {
                        while (!_onFail.isEmpty()) {
                            _onFail.remove().run(reason);
                        }
                    }
                }
            });
        }

        Messenger.debug(_plugin, reason);

        doOnEnd();
    }

    // run onEnd call backs
    private void doOnEnd() {


        boolean isEmpty;

        synchronized (_sync) {
            isEmpty = _onEnd.isEmpty();
        }

        if (!isEmpty) {
            Scheduler.runTaskSync(_plugin, new Runnable() {

                @Override
                public void run() {

                    synchronized (_sync) {

                        while (!_onEnd.isEmpty()) {
                            _onEnd.remove().run();
                        }
                    }
                }
            });
        }
    }

    public class Future {

        /**
         * Add a call back to be called when completed successfully.
         *
         * <p>If already complete, the call back is run on the next
         * available tick.</p>
         *
         * @param runnable  The call back to run
         */
        public Future onComplete(final Runnable runnable) {
            PreCon.notNull(runnable);

            if (_task.isComplete()) {
                Scheduler.runTaskSync(_plugin, new Runnable() {

                    @Override
                    public void run() {

                        synchronized (_sync) {

                            runnable.run();
                        }
                    }
                });
            } else {
                synchronized (_sync) {
                    _onComplete.add(runnable);
                }
            }

            return this;
        }

        /**
         * Add a call back to be called if cancelled.
         *
         * <p>If the task is already cancelled, the call back is run
         * on the next available tick.</p>
         *
         * @param runnable  The call back to run
         */
        public Future onCancel(final CancelHandler runnable) {
            PreCon.notNull(runnable);

            if (_task.isCancelled()) {
                Scheduler.runTaskSync(_plugin, new Runnable() {

                    @Override
                    public void run () {

                        synchronized (_sync) {

                            runnable.run(_cancelReason);
                        }
                    }
                });
            } else {
                synchronized (_sync) {
                    _onCancel.add(runnable);
                }
            }

            return this;
        }

        /**
         * Add a call back to be called if the task fails.
         *
         * <p>If the task has already failed, the call back is run
         * on the next available tick.</p>
         *
         * @param runnable  The call back to run
         */
        public Future onFail(final FailHandler runnable) {
            PreCon.notNull(runnable);

            if (_task.isFailed()) {
                Scheduler.runTaskSync(_plugin, new Runnable() {

                    @Override
                    public void run () {

                        synchronized (_sync) {
                            runnable.run(_failReason);
                        }
                    }

                });
            } else {
                synchronized (_sync) {
                    _onFail.add(runnable);
                }
            }

            return this;
        }

        /**
         * Add a call back to be called when the task
         * ends for any reason.
         *
         * <p>If the task has already ended, the call back is run
         * on the next available tick.</p>
         *
         * @param runnable  The call back to run
         */
        public Future onEnd(final Runnable runnable) {
            PreCon.notNull(runnable);

            if (_task.isEnded()) {
                Scheduler.runTaskSync(_plugin, new Runnable() {

                    @Override
                    public void run() {

                        synchronized (_sync) {
                            runnable.run();
                        }
                    }
                });
            } else {
                synchronized (_sync) {
                    _onEnd.add(runnable);
                }
            }

            return this;
        }
    }


    public static interface CancelHandler {
        void run(@Nullable String reason);
    }

    public static interface FailHandler {
        void run(@Nullable String reason);
    }
}
