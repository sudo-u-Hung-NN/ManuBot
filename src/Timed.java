public interface Timed {
    /**
     * This function indicates whether an object is going
     * to do a specific timed-action
     * @param timeNow: network time
     * @return true: do the work, false: keep waiting
     */
    boolean isActivated(double timeNow);
}
