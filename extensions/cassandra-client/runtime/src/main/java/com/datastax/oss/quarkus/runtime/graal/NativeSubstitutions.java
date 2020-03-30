package com.datastax.oss.quarkus.runtime.graal;



import com.datastax.oss.driver.internal.core.os.Native;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;


@TargetClass(Native.class)
final class NativeSubstitutions {

    /**
     * This method returns always false because jnr is not available.
     *
     * @return false denoting that {@link Native.getProcessId()} cannot be called.
     */
    @Substitute
    public static boolean isGetProcessIdAvailable() {
        return false;
    }
}
