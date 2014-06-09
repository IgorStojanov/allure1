package ru.yandex.qatools.allure.events;

import ru.yandex.qatools.allure.model.Failure;
import ru.yandex.qatools.allure.model.Status;
import ru.yandex.qatools.allure.model.TestCaseResult;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 13.05.14
 *         <p/>
 *         Abstract TestCase status event
 * @see ru.yandex.qatools.allure.events.TestCaseFailureEvent
 * @see ru.yandex.qatools.allure.events.TestCaseCanceledEvent
 * @see ru.yandex.qatools.allure.events.TestCasePendingEvent
 */
public abstract class TestCaseStatusChangeEvent extends AbstractTestCaseStatusChangeEvent {

    /**
     * Returns the status, used in {@link #process(TestCaseResult)}
     * to change TestCaseResult status
     *
     * @return the status
     */
    protected abstract Status getStatus();

    /**
     * Returns default message, used if throwable not specified
     *
     * @return default message
     */
    protected abstract String getMessage();

    /**
     * Change status in specified testCase. If throwable not specified uses
     * {@link #getDefaultFailure()} and {@link #getFailure()} otherwise
     *
     * @param testCase to change
     */
    @Override
    public void process(TestCaseResult testCase) {
        testCase.setStatus(getStatus());
        testCase.setFailure(throwable == null ? getDefaultFailure() : getFailure());
    }

    /**
     * Create failure from throwable using {@link #getMessage(Throwable)} and {@link #getStackTrace(Throwable)}
     *
     * @return created failure
     */
    private Failure getFailure() {
        return new Failure()
                .withMessage(getMessage(getThrowable()))
                .withStackTrace(getStackTrace(getThrowable()));
    }

    /**
     * Gets a short message summarising the exception.
     * <p/>
     * The message returned is of the form
     * {ClassNameWithoutPackage}: {ThrowableMessage}
     *
     * @param throwable the throwable to get a message for
     * @return the message
     */
    private String getMessage(Throwable throwable) {
        return String.format("%s: %s", throwable.getClass().getSimpleName(), throwable.getMessage());
    }

    /**
     * <p>Gets the stack trace from a Throwable as a String.</p>
     * <p/>
     * <p>The result of this method vary by JDK version as this method
     * uses {@link Throwable#printStackTrace(java.io.PrintWriter)}.
     * On JDK1.3 and earlier, the cause exception will not be shown
     * unless the specified throwable alters printStackTrace.</p>
     *
     * @param throwable the <code>Throwable</code> to be examined
     * @return the stack trace as generated by the exception's
     * <code>printStackTrace(PrintWriter)</code> method
     */
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Create default failure (if throwable not specified)
     *
     * @return default failure
     */
    private Failure getDefaultFailure() {
        return new Failure()
                .withMessage(getMessage())
                .withStackTrace("There are no stack trace");
    }
}
